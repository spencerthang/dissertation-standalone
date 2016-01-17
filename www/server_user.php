<?php
	session_start();
	$data = array(
		"Nonce" => session_id(),
		"ServiceUri" => "http://127.0.0.1/pico/server.php",
		"ServiceName" => "Test Service"
	);
?>

Scan the following QR code with the Pico app to continue: <br />
<img src="qr.php?base64_data=<?php echo base64_encode(json_encode($data)); ?>">
