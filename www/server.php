<?php

require_once("key_config.php");

class AuthenticationProtocol
{
    const HEADER_SERVER_HANDSHAKE = 3;
    const HEADER_SERVER_CHALLENGE = 4;
    const HEADER_SERVER_CHALLENGE_RESPONSE = 5;
    const HEADER_SERVER_AUTHENTICATION_STATUS = 6;
    const HEADER_SERVER_LOGIN_MESSAGE = 7;
    const HEADER_SERVER_LOGIN_MESSAGE_RESPONSE = 8;
    const HEADER_SERVER_USER_MESSAGE = 10;
    const HEADER_SERVER_USER_MESSAGE_RESPONSE = 11;
}

// The server will accept well-formed HTTP POST requests only.
// Decrypt the message if necessary
if(isset($_POST['data'])) {
    $data = json_decode($_POST['data'], true);
} elseif(isset($_POST['encryptedData'])) {
    $data = json_decode($_POST['encryptedData'], true);

    if(!isset($data['serverSessionId']) || !isset($data['iv']) || !isset($data['mac'])) {
        result_error('Malformed request - insufficient information to decode request.' . $_POST['encryptedData']);
    }

    // Initialize the previous session
    session_id($data['serverSessionId']);
    session_start();

    if(!isset($_SESSION['sessionKey'])) {
        result_error('Invalid session, session may have expired.');
    }

    $data = decryptMessage($data['encryptedData'], $data['iv'], $data['mac'], $_SESSION['sessionKey']);
    if($data === false || $data === null) {
        result_error('Malformed request - failed to decode request.');
    }

    $data = json_decode($data, true);
} else {
    result_error('No data provided.');
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
            'serverSessionId' => session_id()
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
    case AuthenticationProtocol::HEADER_SERVER_LOGIN_MESSAGE:
        if(!isset($_SESSION['authenticated']) || !$_SESSION['authenticated']) {
            result_error('Protocol authentication failure.');
        }

        $sessionKey = $_SESSION['sessionKey'];

        // Begin login process
        $loginStatus = array(
            'header' => AuthenticationProtocol::HEADER_SERVER_LOGIN_MESSAGE_RESPONSE,
            'loggedIn' => false
        );

        if(isset($data['serverSessionNonce']) // which session to login?
            && isset($data['username']) // username and password
            && isset($data['password'])) {

            // Verify username and password
            if($data['username'] == 'symmetric'
                && $data['password'] == 'auth') {
                session_write_close();
                session_id($data['serverSessionNonce']);
                session_start();
                $_SESSION['loggedIn'] = true;
                $loginStatus['loggedIn'] = true;
            } else {
                result_error('Login failed: invalid username or password.');
            }
        } else {
            result_error('Login failed: insufficient data provided.');
        }

        result($loginStatus, $sessionKey);

        break;
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
	$enc_key = substr(hash("sha256", $key . 'e', true), 0, 16);
	$hash_key = substr(hash("sha256", $key . 'm', true), 0, 16);
    $data = openssl_encrypt(json_encode($data), PICO_CIPHER, $enc_key, OPENSSL_RAW_DATA, $iv);
    $result = array(
        'encryptedData' => base64_encode($data),
        'length' => mb_strlen($data, '8bit'),
        'iv' => base64_encode($iv),
        'mac' => base64_encode(hash_hmac(HMAC_CIPHER, $iv . $data, $hash_key, true))
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