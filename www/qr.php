<?php
require_once("lib/qr/qrlib.php");

// outputs image directly into browser, as PNG stream 
if(isset($_GET['base64_data']))
	QRcode::png(base64_decode($_GET['base64_data']));
else
	QRcode::png("No pico data available.");

?>