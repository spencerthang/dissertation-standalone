<?php

if(isset($_GET['generate'])) {
    $key = openssl_random_pseudo_bytes(16);
    var_dump(base64_encode($key));
}

$keys = array();
$keys[0] = base64_decode('fB6hisXVQU4fZkZ59x6v0A=='); // KDC Key
$keys[1] = base64_decode('eCd2T3UxOG8WfbuTm2DxiQ=='); // Client Key
$keys[2] = base64_decode('h98dZzwkug6PBOryUrBlxA=='); // Server Key

define("PICO_CIPHER", "aes-128-gcm");

?>