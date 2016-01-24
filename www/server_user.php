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
You are <?php echo (isset($_SESSION["authenticated"]) && $_SESSION["authenticated"] === true) ? "authenticated" : "unauthenticated"; ?>.<br />
Scan the following QR code with the Pico app to continue: <br /><br />
<div id="qrcode"></div><br /><br />

This page will refresh in <span id="countdown"></span> seconds, or <a href='#' onclick='location.reload(true); return false;'>refresh now</a>.

<script type="text/javascript" src="qrcode.js"></script>
<script type="text/javascript">
(function countdown(remaining) {
	if(remaining === 0)
		location.reload(true);
	document.getElementById('countdown').innerHTML = remaining;
	setTimeout(function(){ countdown(remaining - 1); }, 1000);
})(5);
new QRCode(document.getElementById("qrcode"), '<?php echo json_encode($data); ?>');
</script>