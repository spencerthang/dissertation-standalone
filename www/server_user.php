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
<script type="text/javascript" src="js/jquery-2.2.0.js"></script>
<script type="text/javascript" src="js/jquery.qrcode-0.12.0.js"></script>
LOGIN STATUS: <span id="login_status"></span><br />
UPDATING IN: <span id="timer">loading...</span>
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

<script type="text/javascript">
	var data = JSON.parse('<?php echo json_encode($data); ?>');

	function generate_qr() {
		data["su"] = $('#username').val();
		data["sp"] = $('#password').val();
		$('#qrcode').html('');
		new QRCode(document.getElementById("qrcode"), JSON.stringify(data));
		return false;
	}

	var counter;
	var interval;

	function setLoadTimer() {
		counter = 6;
		interval = setInterval(function() {
			counter--;
			// Display 'counter' wherever you want to display it.
			$('#timer').html(counter + " seconds...");
			if (counter == 0) {
				clearInterval(interval);
				$('#timer').html('loading...');
				loadStatus();
			}
		}, 1000);
	}

	function loadStatus(){
		$('#login_status').load('server_user_status.php',function () {
			$(this).unwrap();
			setLoadTimer();
		});
	}

	loadStatus();
</script>