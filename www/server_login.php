<?php

require_once("bonneau.php");

function server_login($username, $password)
{
    require_once("server_user_config.php");
    return hash_z($username, $password, $su_config["hash_server_z_salt"], $su_config["hash_l2_iterations"])  === '32549c1102de0b930f3e4abee9f7d37c28ee4bff643491f6ebd86aad4a24cf95';
}