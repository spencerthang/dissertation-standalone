<?php

require_once("bonneau.php");

function server_login($username, $password)
{
    require_once("server_user_config.php");
	
	// Perform hash y/z as per Bonneau
	$y = hash_y($username, $su_config["sn"], $su_config["hash_server_y_salt"], $su_config["hash_l2_iterations"]);
	$z = hash_z($username, $password, $su_config["hash_server_z_salt"], $su_config["hash_l2_iterations"]);
	
	// Let username and password be y/z
	$username = substr($y, 0, 60);
	$password = $z;

	// Perform auth without logging in
	$wp_user = wp_authenticate_username_password(NULL, $username, $password);
	return !is_wp_error($wp_user);
}

?>