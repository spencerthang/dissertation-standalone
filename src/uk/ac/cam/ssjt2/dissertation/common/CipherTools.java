package uk.ac.cam.ssjt2.dissertation.common;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;

/**
 * Created by Spencer on 4/11/2015.
 */
public class CipherTools {

    public static final String CipherTransformation = "AES/CBC/PKCS5Padding";
    public static final String CipherAlgorithm = "AES";
    public static final String CipherMac = "HMac-SHA256";
    public static final int CipherIVSize = 16;
    private final Cipher m_EncryptCipher;
    private final Cipher m_DecryptCipher;
    private final Mac m_Mac;

    public CipherTools(SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        m_EncryptCipher = Cipher.getInstance(CipherTransformation, "BC");
        m_DecryptCipher = Cipher.getInstance(CipherTransformation, "BC");
        m_EncryptCipher.init(Cipher.ENCRYPT_MODE, key, iv);
        m_DecryptCipher.init(Cipher.DECRYPT_MODE, key, iv);
        m_Mac = Mac.getInstance(CipherMac, "BC");
        m_Mac.init(key);
    }

    public byte[] mac(byte[] encrypted) {
        m_Mac.reset();
        m_Mac.update(encrypted);
        return m_Mac.doFinal();
    }

    public byte[] encrypt(byte[] unencrypted) throws BadPaddingException, IllegalBlockSizeException {
        return m_EncryptCipher.doFinal(unencrypted);
    }

    public byte[] decrypt(byte[] encrypted) throws BadPaddingException, IllegalBlockSizeException {
        return m_DecryptCipher.doFinal(encrypted);
    }

    public static SecretKey GenerateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance(CipherAlgorithm);
        generator.init(128);
        return generator.generateKey();
    }

    public static IvParameterSpec GenerateIV() throws NoSuchAlgorithmException {
        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] iv = new byte[CipherIVSize];
        randomSecureRandom.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

}
