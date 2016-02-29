<?php

define("PICO_CIPHER", "AES-128-CBC");
define("HMAC_CIPHER", "sha256");
define("KEY_SIZE", 16);
define("IV_SIZE", openssl_cipher_iv_length(PICO_CIPHER));

if(isset($_GET['generate'])) {
    $key = openssl_random_pseudo_bytes(KEY_SIZE);
    var_dump(base64_encode($key));
}

$keys = array();
$keys[0] = base64_decode('fB6hisXVQU4fZkZ59x6v0A=='); // KDC Key
$keys['Test Client'] = base64_decode('eCd2T3UxOG8WfbuTm2DxiQ=='); // Client Key
$keys['Test Service'] = base64_decode('h98dZzwkug6PBOryUrBlxA=='); // Server Key
$keys['Wordpress'] = base64_decode('67b5bGY4LnMN/B/aVqdvNw==') // Wordpress Server Key

?>