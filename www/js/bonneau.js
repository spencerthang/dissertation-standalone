var Hash = function() {

}

// Underlying hash is defined to be SHA256.
Hash.underlying_hash = function(str) {
    var shaObj = new jsSHA("SHA-256", "TEXT");
    shaObj.update(str);
    return shaObj.getHash("HEX");
}

Hash.hash_b = function(str, salt, iterations) {
    ret = "";
    for (i = 0; i < iterations; i++) {
        ret = str + ret;
        ret = this.underlying_hash(salt + i + ret);
    }
    return ret;
}

Hash.hash_x = function(user, pass, server, salt, iterations) {
    return this.hash_b(user + pass + server, salt, iterations);
}

Hash.hash_y = function(user, server, salt, iterations) {
    return this.hash_b(user + server, salt, iterations);
}

Hash.hash_z = function(user, hash_x, salt, iterations) {
    return this.hash_b(user + hash_x, salt, iterations);
}