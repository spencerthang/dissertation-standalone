<?php

require_once("key_config.php");

class AuthenticationProtocol
{
    const HEADER_KDC_REQUEST = 1;
    const HEADER_KDC_RESPONSE = 2;
}

// The KDC will accept well-formed HTTP POST requests only.
if(!isset($_POST['Data']))
    result_error('No input provided.');

$data = json_decode($_POST['Data'], true);

// Check if JSON decode was successful
if($data === null) {
    result_error('Malformed request - invalid JSON.');
}

// Check for the presence of a header
if(!isset($data['Header'])) {
    result_error('Request payload did not provide a header.');
}

switch($data['Header']) {
    case AuthenticationProtocol::HEADER_KDC_REQUEST:
        // Parse input from client
        if(!isset($data['ClientId']) || !isset($data['TargetId']) || !isset($data['ClientNonce'])) {
            result_error('Malformed KDC request.');
        }

        $clientId = $data['ClientId'];
        $targetId = $data['TargetId'];
        $clientNonce = $data["ClientNonce"];

        // Obtain keys
        if(!isset($keys[$clientId])) result_error('Client not found.');
        if(!isset($keys[$targetId])) result_error('Target not found.');
        $clientKey = $keys[$clientId];
        $targetKey = $keys[$targetId];

        // Generate session key and IVs
        $sessionKey = openssl_random_pseudo_bytes(KEY_SIZE);
        $targetIV = openssl_random_pseudo_bytes(IV_SIZE);

        // Generate encrypted token for the target to verify client
        $targetMessage = array(
            "SessionKey" => base64_encode($sessionKey),
            "ClientId" => $clientId,
        );
        $targetMessageEncrypted = openssl_encrypt(json_encode($targetMessage), PICO_CIPHER, $targetKey, OPENSSL_RAW_DATA, $targetIV);
        $targetMessage = array(
            "Data" => base64_encode($targetMessageEncrypted),
            "IV" => base64_encode($targetIV),
            "HMAC" => base64_encode(hash_hmac(HMAC_CIPHER, $targetMessageEncrypted, $targetKey, true))
        );

        // Generate encrypted response for client
        $clientResponse = array(
            "Header" => AuthenticationProtocol::HEADER_KDC_RESPONSE,
            "SessionKey" => base64_encode($sessionKey),
            "TargetId" => $data['TargetId'],
            "ClientNonce" => $clientNonce,
            "TargetMessage" => base64_encode(json_encode($targetMessage)),
        );

        result($clientResponse, $clientKey);

        break;
    default:
        result_error('Request payload had unknown header: ' . $data['Header']);
}

function result_error($error) {
    $result = array('Error' => $error);
    die(json_encode($result));
}

function result($data, $key) {
    $iv = openssl_random_pseudo_bytes(IV_SIZE);
    $data = openssl_encrypt(json_encode($data), PICO_CIPHER, $key, OPENSSL_RAW_DATA, $iv);
    $result = array(
        'Data' => base64_encode($data),
        'Length' => mb_strlen($data, '8bit'),
        'IV' => base64_encode($iv),
        'HMAC' => base64_encode(hash_hmac(HMAC_CIPHER, $data, $key, true))
    );
    die(json_encode($result));
}


?>