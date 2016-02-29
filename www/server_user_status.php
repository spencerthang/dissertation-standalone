<?php
session_start();
require_once("server_user_config.php");

if(isset($_SESSION["loggedIn"]) && isset($_SESSION["username"]) && isset($_SESSION["password"]) && $_SESSION["loggedIn"] === true) {
	// Perform hashing	
	$y = hash_y($_SESSION["username"], $su_config["sn"], $su_config["hash_server_y_salt"], $su_config["hash_l2_iterations"]);
	$z = hash_z($_SESSION["password"], $password, $su_config["hash_server_z_salt"], $su_config["hash_l2_iterations"]);
	
	// Let username and password be y/z
	$username = substr($y, 0, 60);
	$password = $z;
	
	$creds = array('user_login' => $username,
				   'user_password' => $password,
				   'remember' => true);
	$wp_user = wp_signon($creds, true);
	
	// Return result, including redirect
    echo "<span style='color:green'>logged in</span><script language='javascript'>window.location = '/wordpress';</script>";
} else {
    echo "<span style='color:red'>not logged in</span>";
}
?>