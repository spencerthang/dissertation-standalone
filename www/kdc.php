<?php

require_once("key_config.php");

class AuthenticationProtocol
{
    const HEADER_KDC_REQUEST = 1;
    const HEADER_KDC_RESPONSE = 2;
}

// The KDC will accept well-formed HTTP POST requests only.
if(!isset($_POST['data']))
    result_error('No input provided.');

$data = json_decode($_POST['data'], true);

// Check if JSON decode was successful
if($data === null) {
    result_error('Malformed request - invalid JSON.');
}

// Check for the presence of a header
if(!isset($data['header'])) {
    result_error('Request payload did not provide a header.');
}

switch($data['header']) {
    case AuthenticationProtocol::HEADER_KDC_REQUEST:
        // Parse input from client
        if(!isset($data['clientName']) || !isset($data['targetName']) || !isset($data['clientNonce'])) {
            result_error('Malformed KDC request.');
        }

        $clientName = $data['clientName'];
        $targetName = $data['targetName'];
        $clientNonce = $data['clientNonce'];

        // Obtain keys
        if(!isset($keys[$clientName])) result_error('Client not found.');
        if(!isset($keys[$targetName])) result_error('Target not found.');
        $clientKey = $keys[$clientName];
        $targetKey = $keys[$targetName];

        // Generate session key and IVs
        $sessionKey = openssl_random_pseudo_bytes(KEY_SIZE);
        $targetIV = openssl_random_pseudo_bytes(IV_SIZE);

        // Generate encrypted token for the target to verify client
        $targetMessage = array(
            "sessionKey" => base64_encode($sessionKey),
            "clientName" => $clientName,
        );
        $targetMessageEncrypted = openssl_encrypt(json_encode($targetMessage), PICO_CIPHER, $targetKey, OPENSSL_RAW_DATA, $targetIV);
        $targetMessage = array(
            "encryptedData" => base64_encode($targetMessageEncrypted),
            "iv" => base64_encode($targetIV),
            "mac" => base64_encode(hash_hmac(HMAC_CIPHER, $targetMessageEncrypted, $targetKey, true))
        );

        // Generate encrypted response for client
        $clientResponse = array(
            "header" => AuthenticationProtocol::HEADER_KDC_RESPONSE,
            "sessionKey" => base64_encode($sessionKey),
            "targetName" => $data['targetName'],
            "clientNonce" => $clientNonce,
            "targetMessage" => base64_encode(json_encode($targetMessage)),
        );

        result($clientResponse, $clientKey);

        break;
    default:
        result_error('Request payload had unknown header: ' . $data['header']);
}

function result_error($error) {
    $result = array('error' => $error);
    die(json_encode($result));
}

function result($data, $key) {
    $iv = openssl_random_pseudo_bytes(IV_SIZE);
    $data = openssl_encrypt(json_encode($data), PICO_CIPHER, $key, OPENSSL_RAW_DATA, $iv);
    $result = array(
        'encryptedData' => base64_encode($data),
        'length' => mb_strlen($data, '8bit'),
        'iv' => base64_encode($iv),
        'mac' => base64_encode(hash_hmac(HMAC_CIPHER, $iv . $data, $key, true))
    );
    die(json_encode($result));
}


?>