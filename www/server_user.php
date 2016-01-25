<?php
	require_once("server_user_config.php");
	require_once("bonneau.php");
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
<script type="text/javascript" src="js/sha.js"></script>
<script type="text/javascript" src="js/bonneau.js"></script>
LOGIN STATUS: <span id="login_status"></span><br />
UPDATING IN: <span id="timer">loading...</span>
<br /><br />

<form>
	Username: <input id="username" /><br />
	Password: <input id="password" /><br />
	<input type="Submit" value="Generate QR Code" onclick="return login_submit();" />
</form>

<br /><br />

<?php

echo 'server_hash_x(symmetric, auth, ' . $su_config["sn"] . ' ,' . $su_config["hash_client_salt"] . ' ,' . $su_config["hash_l1_iterations"] . ') = ';
echo hash_x('symmetric', 'auth', $su_config["sn"], $su_config["hash_client_salt"], $su_config["hash_l1_iterations"]);

?>

<div id="hash_log"></div>

<br /><br />

Scan the following QR code with the Pico app to continue: 

<br /><br />

<div id="qrcode"></div>

<script type="text/javascript">
	var data = JSON.parse('<?php echo json_encode($data); ?>');
	var service_name = '<?php echo $su_config["sn"]; ?>';
	var hash_client_salt = '<?php echo $su_config["hash_client_salt"]; ?>';
	var hash_l1_iterations = <?php echo $su_config["hash_l1_iterations"]; ?>;

	function login_submit() {
		username = $('#username').val();
		password = $('#password').val();

		// generate hash
		hashx = Hash.hash_x(username, password, service_name, hash_client_salt, hash_l1_iterations);

		$('#hash_log').html('client_hash_x(' + username + ', ' + password + ', ' + service_name + ', ' + hash_client_salt + ', ' + hash_l1_iterations + ') = ' + hashx);

		data["su"] = username;
		data["sp"] = hashx;
		generate_qr(data);
		return false;
	}

	function generate_qr(data) {
		$('#qrcode').html('');
		$('#qrcode').qrcode({
			"size": 200,
			"color": "#3a3",
			"text": JSON.stringify(data)
		});
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