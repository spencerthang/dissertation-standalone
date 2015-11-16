<?php

if(isset($_GET['generate'])) {
    $key = openssl_random_pseudo_bytes(16);
    var_dump(bin2hex($key));
}

$keys = array();
$keys[0] = hex2bin('2ea7c638113ca95f2d2983456eb537a1'); // KDC Key
$keys[1] = hex2bin('74115315cd9ba1e08ee702c6d6a5750a'); // Client Key
$keys[2] = hex2bin('b517ed4d81dbf249b39552cf0055094c'); // Server Key

define("PICO_CIPHER", "aes-128-gcm");

?>