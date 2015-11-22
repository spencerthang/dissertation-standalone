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
    result_error('No input provided.');

$data = $_POST['data'];

// Perform decryption if necessary.
if(isset($_POST['encrypted'])) {
    $data = array();
} else {
    $data = json_decode($data, true);
}

// Check for the presence of a header
if(!isset($data['Header'])) {
    result_error('Request payload did not provide a header.');
}

switch($data['Header']) {
    case AuthenticationProtocol::HEADER_KDC_REQUEST:
        $output = '';

        // Parse input from client
        $clientId = $data['ClientId'];
        $targetId = $data["TargetId"];
        $clientNonce = $data["ClientNonce"];

        // Obtain keys
        if(!isset($keys[$clientId])) result_error('Client not found.');
        if(!isset($keys[$targetId])) result_error('Target not found.');
        $clientKey = $keys[$clientId];
        $targetKey = $keys[$targetId];

        // Generate session key and IVs
        $sessionKey = openssl_random_pseudo_bytes(KEY_SIZE);
        $sessionIV = openssl_random_pseudo_bytes(IV_SIZE);
        $targetIV = openssl_random_pseudo_bytes(IV_SIZE);
        $clientIV = openssl_random_pseudo_bytes(IV_SIZE);

        // Generate encrypted token for the target to verify client
        $targetMessage = array(
            "SessionKey" => $sessionKey,
            "SessionIV" => base64_encode($sessionIV),
            "ClientId" => $clientId,
        );
        $targetMessage = openssl_encrypt(json_encode($targetMessage), PICO_CIPHER, $targetKey, OPENSSL_RAW_DATA, $targetIV);

        // Generate encrypted response for client
        $clientResponse = array(
            "SessionKey" => base64_encode($sessionKey),
            "SessionIV" => base64_encode($sessionIV),
            "TargetId" => $data["TargetId"],
            "TargetIV" => base64_encode($sessionIV),
            "ClientNonce" => $clientNonce,
            "TargetMessage" => base64_encode($targetMessage),
        );

        $clientResponse = openssl_encrypt(json_encode($clientResponse), PICO_CIPHER, $clientKey, OPENSSL_RAW_DATA, $clientIV);
        result($clientResponse, $clientIV);

        break;
    default:
        die('Request payload had unknown header: ' + $data['Header']);
}

function result_error($error) {
    $result = array('Error' => $error);
    die(json_encode($result));
}

function result($data, $iv = null) {
    $result = array('Data' => base64_encode($data), 'Length' => mb_strlen($data, '8bit'));
    if($iv !== null) $result['IV'] = base64_encode($iv);
    die(json_encode($result));
}


?>