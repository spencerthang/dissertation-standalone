<?php

require_once("keys.php");

class AuthenticationProtocol
{
    const HEADER_TEST = 10;
    const HEADER_SESSION_ENCRYPTED = 0;
    const HEADER_KDC_REQUEST = 1;
    const HEADER_KDC_RESPONSE = 2;
    const HEADER_SERVER_HANDSHAKE = 3;
    const HEADER_SERVER_CHALLENGE = 4;
    const HEADER_SERVER_CHALLENGE_RESPONSE = 5;
}

// The KDC will accept well-formed HTTP POST requests only.
if(!isset($_POST['data']))
    result(null, 'No input provided.');

$data = $_POST['data'];

// Perform decryption if necessary.
if(isset($_POST['encrypted'])) {
    $data = array();
} else {
    $data = json_decode($data, true);
}

// Check for the presence of a header
if(!isset($data['Header'])) {
    result(null, 'Request payload did not provide a header.');
}

switch($data['Header']) {
    case AuthenticationProtocol::HEADER_KDC_REQUEST:
        $output = '';

        // Parse input from client
        $clientId = $data['ClientId'];
        $targetId = $data["TargetId"];
        $clientNonce = $data["ClientNonce"];

        // Obtain keys
        if(!isset($keys[$clientId])) result(null, 'Client not found.');
        if(!isset($keys[$targetId])) result(null, 'Target not found.');
        $clientKey = $keys[$clientId];
        $targetKey = $keys[$targetId];

            // Generate session key
        $sessionKey = openssl_random_pseudo_bytes(16);
        $sessionIv = openssl_random_pseudo_bytes(12);
        $targetIv = openssl_random_pseudo_bytes(12);

        // Generate encrypted token for the target to verify client
        $targetMessage = array(
            "SessionKey" => $sessionKey,
            "SessionIv" => bin2hex($sessionIv),
            "ClientId" => $clientId,
        );
        $targetMessage = openssl_encrypt(json_encode($targetMessage), PICO_CIPHER, $targetKey, 0, $targetIv);

        // Generate encrypted response for client
        $clientResponse = array(
            "SessionKey" => bin2hex($sessionKey),
            "SessionIv" => bin2hex($sessionIv),
            "TargetId" => $data["TargetId"],
            "TargetIv" => bin2hex($targetIv),
            "ClientNonce" => $clientNonce,
            "TargetMessage" => base64_encode($targetMessage),
        );
        $clientResponse = openssl_encrypt(json_encode($clientResponse), PICO_CIPHER, $targetKey, 0, $targetIv);
        result($clientResponse);

        break;
    default:
        die('Request payload had unknown header: ' + $data['header']);
}

function result($data, $error = null) {
    $result = array();
    if($error !== null) {
        $result['error'] = $error;
    } else {
        $result['data'] = $data;
    }
    die(json_encode($result));
}


?>