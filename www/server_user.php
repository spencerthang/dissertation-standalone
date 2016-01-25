<?php
	require_once("server_user_config.php");
	session_start();
	$data = array(
		"nonce" => session_id(),
		"sn" => $su_config["sn"],
		"sa" => $su_config["sa"],
		"t" => "SA"
	);
?>
LOGIN STATUS: <?php echo (isset($_SESSION["loggedIn"]) && $_SESSION["loggedIn"] === true) ? "<span style='color:green'>logged in</span>" : "<span style='color:red'>not logged in</span>"; ?>
<br /><br />

<form>
	Username: <input id="username" /><br />
	Password: <input id="password" /><br />
	<input type="Submit" value="Generate QR Code" onclick="return generate_qr();" />
</form>

<br /><br />

Scan the following QR code with the Pico app to continue: 

<br /><br />

<div id="qrcode"></div>

<script type="text/javascript" src="qrcode.js"></script>
<script type="text/javascript">
var data = JSON.parse('<?php echo json_encode($data); ?>')

function generate_qr() {
	data["su"] = document.getElementById("username").value;
	data["sp"] = document.getElementById("password").value;
	new QRCode(document.getElementById("qrcode"), JSON.stringify(data));
	return false;
}
</script>