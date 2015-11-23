package uk.ac.cam.ssjt2.dissertation.common;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;

/**
 * Created by Spencer on 4/11/2015.
 */
public class CipherTools {

    public static final String CipherTransformation = "AES/GCM/NoPadding";
    public static final String CipherAlgorithm = "AES";
    public static final int CipherIVSize = 16;
    private final Cipher m_EncryptCipher;
    private final Cipher m_DecryptCipher;

    public CipherTools(SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        m_EncryptCipher = Cipher.getInstance(CipherTransformation, "BC");
        m_DecryptCipher = Cipher.getInstance(CipherTransformation, "BC");
        m_EncryptCipher.init(Cipher.ENCRYPT_MODE, key, iv);
        m_DecryptCipher.init(Cipher.DECRYPT_MODE, key, iv);
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
