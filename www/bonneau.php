<?php

function underlying_hash($str) {
    return hash("sha256", $str);
}

function hash_b($str, $salt, $iterations) {
    $ret = "";
    for ($i = 0; $i < $iterations; $i++) {
        $ret = $str . $ret;
        $ret = underlying_hash($salt . $i . $ret);
    }
    return $ret;
}

function hash_x($user, $pass, $server, $salt, $iterations) {
    return hash_b($user . $pass . $server, $salt, $iterations);
}

function hash_y($user, $server, $salt, $iterations) {
    return hash_b($user . $server, $salt, $iterations);
}

function hash_z($user, $hash_x, $salt, $iterations) {
    return hash_b($user . $hash_x, $salt, $iterations);
}

?>