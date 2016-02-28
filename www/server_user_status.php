<?php
session_start();
if(isset($_SESSION["loggedIn"]) && $_SESSION["loggedIn"] === true) {
    echo "<span style='color:green'>logged in</span>";
} else {
    echo "<span style='color:red'>not logged in</span>";
}
?>