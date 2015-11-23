<?php

require_once("key_config.php");

class AuthenticationProtocol
{
    const HEADER_SERVER_HANDSHAKE = 3;
    const HEADER_SERVER_CHALLENGE = 4;
    const HEADER_SERVER_CHALLENGE_RESPONSE = 5;
}

// The server will accept well-formed HTTP POST requests only.
if(!isset($_POST['Data']))
    result_error('No data provided.');

$data = json_decode($_POST['Data'], true);

// Check for the presence of a header
if(!isset($data['Header'])) {
    result_error('Request payload did not provide a header.');
}

// Set server key
$serverKey = $keys[2];

switch($data['Header']) {
    case AuthenticationProtocol::HEADER_SERVER_HANDSHAKE:
        // Decrypt handshake
        $handshake = json_decode(decryptMessage($data['Handshake'], $serverKey), true);

        $sessionKey = base64_decode($handshake['SessionKey']);
        $clientId = $handshake['ClientId'];

        // Generate server challenge
        $serverChallenge = array(
            "ServerNonce" => mt_rand(0, 2147483647),
            "ClientId" => $clientId,
        );
        result($serverChallenge, $sessionKey);

        break;
    default:
        die('Request payload had unknown header: ' + $data['Header']);
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
        'IV' => base64_encode($iv)
    );
    die(json_encode($result));
}

function decryptMessage($message, $key) {
    $encrypted = json_decode(base64_decode($message), true);
    $data = base64_decode($encrypted['Data']);
    $iv = base64_decode($encrypted['IV']);

    // Decrypt handshake
    $decrypted = openssl_decrypt($data, PICO_CIPHER, $key, OPENSSL_RAW_DATA, $iv);
    return $decrypted;
}


?>