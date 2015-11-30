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
if((!isset($_POST['encryptedData']) || !isset($_POST['iv']) || !isset($_POST['sessionId'])) && !isset($_POST['data']))
    result_error('No data provided.');

// Decrypt the message if necessary
if(!isset($_POST['iv']) || !isset($_POST['sessionId'])) {
    $data = json_decode($_POST['data'], true);
} else {
    // Initialize the previous session
    session_id($_POST['sessionId']);
    session_start();

    if(!isset($_SESSION['sessionKey'])) {
        result_error('Invalid session, session may have expired.');
    }

    $data = decryptMessage($_POST['encryptedData'], $_POST['iv'], $_POST['mac'], $_SESSION['sessionKey']);
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
if(!isset($data['header'])) {
    result_error('Request payload did not provide a header.');
}

// Set server key
$serverKey = $keys['Test Service'];

switch($data['header']) {
    case AuthenticationProtocol::HEADER_SERVER_HANDSHAKE:
        // Decrypt handshake
        $encrypted = json_decode(base64_decode($data['handshake']), true);
        if($encrypted === null || !isset($encrypted['encryptedData']) || !isset($encrypted['iv'])) {
            result_error('Handshake invalid, failed to obtain session key.');
        }
        $decrypted = decryptMessage($encrypted['encryptedData'], $encrypted['iv'], $encrypted['mac'], $serverKey);
        $handshake = json_decode($decrypted, true);
        if($handshake === null || !isset($handshake['sessionKey']) || !isset($handshake['clientName'])) {
            result_error('Handshake invalid, failed to obtain session key.');
        }

        $sessionKey = base64_decode($handshake['sessionKey']);
        $clientId = $handshake['clientName'];

        // Generate a new session and include the ID
        session_start();
        $_SESSION['authenticated'] = false;
        $_SESSION['serverNonce'] = mt_rand(0, 2147483647);
        $_SESSION['sessionKey'] = $sessionKey;

        // Generate server challenge
        $serverChallenge = array(
            'header' => AuthenticationProtocol::HEADER_SERVER_CHALLENGE,
            'serverNonce' => $_SESSION['serverNonce'],
            'clientName' => $clientId,
            'sessionId' => session_id()
        );
        result($serverChallenge, $sessionKey);

        break;
    case AuthenticationProtocol::HEADER_SERVER_CHALLENGE_RESPONSE:
        // Check if nonce matches
        if(isset($_SESSION['serverNonce'])
            && isset($data['serverNonce'])
            && $_SESSION['serverNonce'] == $data['serverNonce']) {
            $_SESSION['authenticated'] = true;
        }

        $authenticationStatus = array(
            'header' => AuthenticationProtocol::HEADER_SERVER_AUTHENTICATION_STATUS,
            'authenticated' => $_SESSION['authenticated']
        );
        result($authenticationStatus, $_SESSION['sessionKey']);

        break;
    // Application code goes here, sent under SERVER_USER_MESSAGE and SERVER_USER_MESSAGE_RESPONSE.
    case AuthenticationProtocol::HEADER_SERVER_USER_MESSAGE:
        if(!isset($_SESSION['authenticated']) || !$_SESSION['authenticated']) {
            result_error('Protocol authentication failure.');
        }

        if(!isset($data['request']) || !isset($data['requestType'])) {
            result_error('Malformed user message, missing request or request type.');
        }

        $request = json_decode($data['request'], true);

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
        result_error('Request payload had unknown header: ' . $data['header']);
}

function result_error($error) {
    $result = array('Error' => $error);
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

function user_result($response) {
    $userResponse = array(
        'header' => AuthenticationProtocol::HEADER_SERVER_USER_MESSAGE_RESPONSE,
        'response' => $response
    );
    result($userResponse, $_SESSION['sessionKey']);
}

function decryptMessage($data, $iv, $hmac, $key) {
    $data = base64_decode($data);
    $iv = base64_decode($iv);
    if(hash_hmac(HMAC_CIPHER, $iv . $data, $key, true) != base64_decode($hmac)) {
        return null;
    }
    $decrypted = openssl_decrypt($data, PICO_CIPHER, $key, OPENSSL_RAW_DATA, $iv);
    return $decrypted;
}


?>