package uk.ac.cam.ssjt2.dissertation.tests;

import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * Created by Spencer on 4/11/2015.
 */
public class CipherToolsTests {

    private final SecretKey m_CipherKey;

    public CipherToolsTests() throws NoSuchAlgorithmException {
        m_CipherKey = CipherTools.GenerateSecretKey();
    }

    @Test
    public void canCreateAESCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        CipherTools cipher = new CipherTools(m_CipherKey);
    }

    @Test
    public void canPerformRoundTripEncryption() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        CipherTools cipher = new CipherTools(m_CipherKey);
        byte[] testBytes = new byte[] { 0x0A, 0x01, 0x09, 0x0C, 0x0D, 0x1F, 0x77, 0x65 };
        byte[] encryptedBytes = cipher.encrypt(testBytes);
        byte[] decryptedBytes = cipher.decrypt(encryptedBytes);
        assertArrayEquals(testBytes, decryptedBytes);
    }
}
