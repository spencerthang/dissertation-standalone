<?php
	
require_once("../bonneau.php");
	
// Test underlying hash works
$hash = underlying_hash("test");
if($hash != "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")
    die("Underlying hash does not return the correct SHA256 hash, returns " .$hash);


// Test single iteration of hash_b is merely a concatenated hash.
$hash_b = hash_b("test", "site", 1);
$manual_hash_b_1 = underlying_hash("site0test");

if($hash_b !== $manual_hash_b_1)
    die("Hash B 1 iteration result incorrect, expected: " . $manual_hash_b_1 . ", actual: " . $hash_b);

// Test two iterations of hash_b.
$hash_b = hash_b("test", "site", 2);
$manual_hash_b_2 = underlying_hash("site1test" . $manual_hash_b_1);

if($hash_b !== $manual_hash_b_2)
    die("Hash B 2 iterations result incorrect, expected: " . $manual_hash_b_1 . ", actual: " . $hash_b);

// Test hash_x returns expected results.
$hash_x_1 = hash_x("user", "pass", "site", "salt", 1);
$hash_x_2 = hash_x("user1", "pass", "site", "salt", 1);
$hash_x_3 = hash_x("user", "pass1", "site", "salt", 1);
$hash_x_4 = hash_x("user", "pass", "site1", "salt", 1);
$hash_x_5 = hash_x("user", "pass", "site", "salt1", 1);
$hash_x_6 = hash_x("user", "pass", "site", "salt", 2);

if($hash_x_1 == $hash_x_2)
    die("Hash X does not change with username");

if($hash_x_1 == $hash_x_3)
    die("Hash X does not change with password");

if($hash_x_1 == $hash_x_4)
    die("Hash X does not change with site identifier");

if($hash_x_1 == $hash_x_5)
    die("Hash X does not change with salt");

if($hash_x_1 == $hash_x_6)
    die("Hash X does not change with iteration count");

$manual_hash_x = hash_b("user" . "pass" . "site", "salt", 1);

if($hash_x_1 != $manual_hash_x)
    die("Hash X 1 iteration result incorrect, expected: " . $manual_hash_x . ", actual: " . $hash_x_1);

// Test hash_y returns expected results.
$hash_y_1 = hash_y("user", "site", "salt", 1);
$hash_y_2 = hash_y("user1", "site", "salt", 1);
$hash_y_3 = hash_y("user", "site1", "salt", 1);
$hash_y_4 = hash_y("user", "site", "salt1", 1);
$hash_y_5 = hash_y("user", "site", "salt", 2);

if($hash_y_1 == $hash_y_2)
    die("Hash Y does not change with username");

if($hash_y_1 == $hash_y_3)
    die("Hash Y does not change with site identifier");

if($hash_y_1 == $hash_y_4)
    die("Hash Y does not change with salt");

if($hash_y_1 == $hash_y_5)
    die("Hash Y does not change with iteration count");

$manual_hash_y = hash_b("user" . "site", "salt", 1);

if($hash_y_1 != $manual_hash_y)
    die("Hash Y 1 iteration result incorrect, expected: " . $manual_hash_y . ", actual: " . $hash_y_1);

// Test hash_z returns expected results.
$hash_z_1 = hash_z("user", "hash_x", "salt", 1);
$hash_z_2 = hash_z("user1", "hash_x", "salt", 1);
$hash_z_3 = hash_z("user", "hash_x1", "salt", 1);
$hash_z_4 = hash_z("user", "hash_x", "salt1", 1);
$hash_z_5 = hash_z("user", "hash_x", "salt", 2);

if($hash_z_1 == $hash_z_2)
    die("Hash Z does not change with username");

if($hash_z_1 == $hash_z_3)
    die("Hash Z does not change with hash_x");

if($hash_z_1 == $hash_z_4)
    die("Hash Z does not change with salt");

if($hash_z_1 == $hash_z_5)
    die("Hash Z does not change with iteration count");

$manual_hash_z = hash_b("user" . "hash_x", "salt", 1);

if($hash_z_1 != $manual_hash_z)
    die("Hash Z 1 iteration result incorrect, expected: " . $manual_hash_z . ", actual: " . $hash_z_1);


die("All tests passed.");

?>