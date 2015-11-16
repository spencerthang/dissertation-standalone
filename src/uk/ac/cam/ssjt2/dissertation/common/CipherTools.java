package uk.ac.cam.ssjt2.dissertation.common;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by Spencer on 4/11/2015.
 */
public class CipherTools {

    public static final String CipherTransformation = "AES";
    private final Cipher m_EncryptCipher;
    private final Cipher m_DecryptCipher;

    public CipherTools(SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        m_EncryptCipher = Cipher.getInstance(CipherTransformation);
        m_DecryptCipher = Cipher.getInstance(CipherTransformation);
        m_EncryptCipher.init(Cipher.ENCRYPT_MODE, key);
        m_DecryptCipher.init(Cipher.DECRYPT_MODE, key);
    }

    public byte[] encrypt(byte[] unencrypted) throws BadPaddingException, IllegalBlockSizeException {
        return m_EncryptCipher.doFinal(unencrypted);
    }

    public byte[] decrypt(byte[] encrypted) throws BadPaddingException, IllegalBlockSizeException {
        return m_DecryptCipher.doFinal(encrypted);
    }

    public static SecretKey GenerateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        return generator.generateKey();
    }

    public static IvParameterSpec GenerateIV() throws NoSuchAlgorithmException {
        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] iv = new byte[12];
        randomSecureRandom.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

}
