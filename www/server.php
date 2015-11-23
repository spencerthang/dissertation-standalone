<?php

require_once("key_config.php");

class AuthenticationProtocol
{
    const HEADER_SERVER_HANDSHAKE = 3;
    const HEADER_SERVER_CHALLENGE = 4;
    const HEADER_SERVER_CHALLENGE_RESPONSE = 5;
    const HEADER_SERVER_AUTHENTICATION_STATUS = 6;
    const HEADER_SERVER_USER_MESSAGE = 10;
    const HEADER_SERVER_USER_MESSAGE_RESPONSE = 11;
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

    if(!isset($_SESSION['SessionKey'])) {
        result_error('Invalid session, session may have expired.');
    }

    $data = decryptMessage($_POST['Data'], $_POST['IV'], $_POST['HMAC'], $_SESSION['SessionKey']);
    if($data === false || $data === null) {
        result_error('Malformed request - failed to decode request.');
    }

    $data = json_decode($data, true);
}

// Check if JSON decode was successful
if($data === null) {
    result_error('Malformed request - invalid JSON.');
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
        if($encrypted === null || !isset($encrypted['Data']) || !isset($encrypted['IV'])) {
            result_error('Handshake invalid, failed to obtain session key.');
        }
        $handshake = json_decode(decryptMessage($encrypted['Data'], $encrypted['IV'], $encrypted['HMAC'], $serverKey), true);
        if($handshake === null || !isset($handshake['SessionKey']) || !isset($handshake['ClientId'])) {
            result_error('Handshake invalid, failed to obtain session key.');
        }

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
        if(isset($_SESSION['ServerNonce'])
            && isset($data['ServerNonce'])
            && $_SESSION['ServerNonce'] == $data['ServerNonce']) {
            $_SESSION['Authenticated'] = true;
        }

        $authenticationStatus = array(
            "Header" => AuthenticationProtocol::HEADER_SERVER_AUTHENTICATION_STATUS,
            "Authenticated" => $_SESSION['Authenticated']
        );
        result($authenticationStatus, $_SESSION['SessionKey']);

        break;
    // Application code goes here, sent under SERVER_USER_MESSAGE and SERVER_USER_MESSAGE_RESPONSE.
    case AuthenticationProtocol::HEADER_SERVER_USER_MESSAGE:
        if(!isset($_SESSION['Authenticated']) || !$_SESSION['Authenticated']) {
            result_error('Protocol authentication failure.');
        }

        if(!isset($data['Request']) || !isset($data['RequestType'])) {
            result_error('Malformed user message, missing request or request type.');
        }

        $request = json_decode($data['Request'], true);

        if(!isset($request['cmd'])) {
            result_error('Malformed user message, missing command.');
        }

        switch($request['cmd']) {
            case 'add':
                user_result($request['x'] + $request['y']);
                break;
            default:
                result_error('Unknown command.');
        }
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

function user_result($response) {
    $userResponse = array(
        'Header' => AuthenticationProtocol::HEADER_SERVER_USER_MESSAGE_RESPONSE,
        'Response' => $response
    );
    result($userResponse, $_SESSION['SessionKey']);
}

function decryptMessage($data, $iv, $hmac, $key) {
    $data = base64_decode($data);
    $iv = base64_decode($iv);
    if(hash_hmac(HMAC_CIPHER, $data, $key, true) != base64_decode($hmac)) {
        return null;
    }
    $decrypted = openssl_decrypt($data, PICO_CIPHER, $key, OPENSSL_RAW_DATA, $iv);
    return $decrypted;
}


?>