<?php

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
    die('No input provided.');

$data = $_POST['data'];

// Perform decryption if necessary.
if(isset($_POST['encrypted'])) {
    $data = array();
} else {
    $data = json_decode($data, true);
}

// Check for the presence of a header
if(!isset($data['Header'])) {
    die('Request payload did not provide a header');
}

switch($data['Header']) {
    case AuthenticationProtocol::HEADER_KDC_REQUEST:
        die("Received KDC request.");
        break;
    default:
        die('Request payload had unknown header: ' + $data['header']);
}


?>