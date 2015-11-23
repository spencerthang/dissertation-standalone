<?php

require_once("key_config.php");

class AuthenticationProtocol
{
    const HEADER_SERVER_HANDSHAKE = 3;
    const HEADER_SERVER_CHALLENGE = 4;
    const HEADER_SERVER_CHALLENGE_RESPONSE = 5;
    const HEADER_SERVER_AUTHENTICATION_STATUS = 6;
}

// The server will accept well-formed HTTP POST requests only.
if(!isset($_POST['Data']))
    result_error('No data provided.');

// Decrypt the message if necessary
if(!isset($_POST['IV']) || !isset($_POST['SessionId'])) {
    $data = json_decode($_POST['Data'], true);
} else {
    // Initialize the previous session
    session_id($_POST['SessionId']);
    session_start();

    $data = decryptMessage($_POST['Data'], $_POST['IV'], $_SESSION['SessionKey']);
    $data = json_decode($data, true);
}

// Check for the presence of a header
if(!isset($data['Header'])) {
    result_error('Request payload did not provide a header.');
}

// Set server key
$serverKey = $keys[2];

switch($data['Header']) {
    case AuthenticationProtocol::HEADER_SERVER_HANDSHAKE:
        // Decrypt handshake
        $encrypted = json_decode(base64_decode($data['Handshake']), true);
        $handshake = json_decode(decryptMessage($encrypted['Data'], $encrypted['IV'], $serverKey), true);

        $sessionKey = base64_decode($handshake['SessionKey']);
        $clientId = $handshake['ClientId'];

        // Generate a new session and include the ID
        session_start();
        $_SESSION['Authenticated'] = false;
        $_SESSION['ServerNonce'] = mt_rand(0, 2147483647);
        $_SESSION['SessionKey'] = $sessionKey;

        // Generate server challenge
        $serverChallenge = array(
            "Header" => AuthenticationProtocol::HEADER_SERVER_CHALLENGE,
            "ServerNonce" => $_SESSION['ServerNonce'],
            "ClientId" => $clientId,
            "SessionId" => session_id()
        );
        result($serverChallenge, $sessionKey);

        break;
    case AuthenticationProtocol::HEADER_SERVER_CHALLENGE_RESPONSE:
        // Check if nonce matches
        if($_SESSION['ServerNonce'] == $data['ServerNonce']) {
            $_SESSION['Authenticated'] = true;
        }

        $authenticationStatus = array(
            "Header" => AuthenticationProtocol::HEADER_SERVER_AUTHENTICATION_STATUS,
            "Authenticated" => $_SESSION['Authenticated']
        );
        result($authenticationStatus, $_SESSION['SessionKey']);

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
        'IV' => base64_encode($iv)
    );
    die(json_encode($result));
}

function decryptMessage($data, $iv, $key) {
    $data = base64_decode($data);
    $iv = base64_decode($iv);
    $decrypted = openssl_decrypt($data, PICO_CIPHER, $key, OPENSSL_RAW_DATA, $iv);
    return $decrypted;
}


?>