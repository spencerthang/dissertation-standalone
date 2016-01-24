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
Scan the following QR code with the Pico app to continue: <br />
<img src="qr.php?base64_data=<?php echo base64_encode(json_encode($data)); ?>"><br />

This page will refresh in <span id="countdown"></span> seconds, or <a href='#' onclick='location.reload(true); return false;'>refresh now</a>.

<script language="javascript">
	(function countdown(remaining) {
		if(remaining === 0)
			location.reload(true);
		document.getElementById('countdown').innerHTML = remaining;
		setTimeout(function(){ countdown(remaining - 1); }, 1000);
	})(5);
</script>
