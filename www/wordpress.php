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
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
	<meta name="description" content="">
	<meta name="author" content="">
	<link rel="icon" href="../../favicon.ico">

	<title>Signin Template for Bootstrap</title>

	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">

	<!-- Provided by https://getbootstrap.com/examples/signin/ -->
	<style type="text/css">
        /*!
         * IE10 viewport hack for Surface/desktop Windows 8 bug
         * Copyright 2014-2015 Twitter, Inc.
         * Licensed under MIT (https://github.com/twbs/bootstrap/blob/master/LICENSE)
         */

        /*
         * See the Getting Started docs for more information:
         * http://getbootstrap.com/getting-started/#support-ie10-width
         */
        @-webkit-viewport { width: device-width; }
        @-moz-viewport    { width: device-width; }
        @-ms-viewport     { width: device-width; }
        @-o-viewport      { width: device-width; }
        @viewport         { width: device-width; }

        /* Actual CSS */
        body {
			padding-top: 40px;
			padding-bottom: 40px;
			background-color: #eee;
		}

		.form-signin {
			max-width: 330px;
			padding: 15px;
			margin: 0 auto;
		}
		.form-signin .form-signin-heading,
		.form-signin .checkbox {
			margin-bottom: 10px;
		}
		.form-signin .checkbox {
			font-weight: normal;
		}
		.form-signin .form-control {
			position: relative;
			height: auto;
			-webkit-box-sizing: border-box;
			-moz-box-sizing: border-box;
			box-sizing: border-box;
			padding: 10px;
			font-size: 16px;
		}
		.form-signin .form-control:focus {
			z-index: 2;
		}
		.form-signin input[type="email"] {
			margin-bottom: -1px;
			border-bottom-right-radius: 0;
			border-bottom-left-radius: 0;
		}
		.form-signin input[type="password"] {
			margin-bottom: 10px;
			border-top-left-radius: 0;
			border-top-right-radius: 0;
		}

        .qrcode {
            margin-top:10px;
            text-align:center;
            display:none;
        }
	</style>

	<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	<!--[if lt IE 9]>
	<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->

    <script type="text/javascript" src="js/jquery-2.2.0.js"></script>
    <script type="text/javascript" src="js/jquery.qrcode-0.12.0.js"></script>
    <script type="text/javascript" src="js/sha.js"></script>
    <script type="text/javascript" src="js/bonneau.js"></script>
</head>

<body>

<div class="container">

	<form class="form-signin">
        <h2 class="form-signin-heading">Wordpress Pico Login</h2>
		<h3 class="form-signin-heading">Status: <span id="login_status"></span> (<span id="timer">...</span>)</h3>
		<label for="username" class="sr-only">Username</label>
		<input type="text" id="username" class="form-control" placeholder="Username" required autofocus>
		<label for="password" class="sr-only">Password</label>
		<input type="text" id="password" class="form-control" placeholder="Password" required>
        <div id="qrcode" class="qrcode form-control"></div>
	</form>

</div> <!-- /container -->

<script type="text/javascript">
    var data = JSON.parse('<?php echo json_encode($data); ?>');
    var service_name = '<?php echo $su_config["sn"]; ?>';
    var hash_client_salt = '<?php echo $su_config["hash_client_salt"]; ?>';
    var hash_l1_iterations = <?php echo $su_config["hash_l1_iterations"]; ?>;

    function login_submit() {
        username = $('#username').val();
        password = $('#password').val();

        if(username.length <= 0 || password.length <= 0) {
            $('#qrcode').hide();
            return;
        }

        // generate hash
        hashx = Hash.hash_x(username, password, service_name, hash_client_salt, hash_l1_iterations);
        data["su"] = username;
        data["sp"] = hashx;

        // generate qr
        generate_qr(data);

        return false;
    }

    function generate_qr(data) {
        $('#qrcode').html('<h4 class="form-signin-heading">Scan QR code to login:</h4>');
        $('#qrcode').show();
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
            $('#timer').html(counter);
            if (counter == 0) {
                clearInterval(interval);
                $('#timer').html('...');
                loadStatus();
            }
        }, 1000);
    }

    function loadStatus(){
        $('#login_status').load('server_user_status.php',function () {
            setLoadTimer();
        });
    }

    $(document).ready(function() {
        $('#username')[0].oninput = login_submit;
        $('#password')[0].oninput = login_submit;
        loadStatus();
    });
</script>

<script language="javascript">
    /*!
     * IE10 viewport hack for Surface/desktop Windows 8 bug
     * Copyright 2014-2015 Twitter, Inc.
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/master/LICENSE)
     */

    // See the Getting Started docs for more information:
    // http://getbootstrap.com/getting-started/#support-ie10-width

    (function () {
        'use strict';

        if (navigator.userAgent.match(/IEMobile\/10\.0/)) {
            var msViewportStyle = document.createElement('style')
            msViewportStyle.appendChild(
                document.createTextNode(
                    '@-ms-viewport{width:auto!important}'
                )
            )
            document.querySelector('head').appendChild(msViewportStyle)
        }

    })();
</script>
</body>
</html>
