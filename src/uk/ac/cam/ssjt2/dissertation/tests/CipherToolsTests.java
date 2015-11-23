package uk.ac.cam.ssjt2.dissertation.tests;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
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
    public void canCreateAESCipher() throws GeneralSecurityException {
        CipherTools cipher = new CipherTools(m_CipherKey, CipherTools.GenerateIV());
    }

    @Test
    public void canPerformRoundTripEncryption() throws GeneralSecurityException {
        CipherTools cipher = new CipherTools(m_CipherKey, CipherTools.GenerateIV());
        byte[] testBytes = new byte[] { 0x0A, 0x01, 0x09, 0x0C, 0x0D, 0x1F, 0x77, 0x65 };
        byte[] encryptedBytes = cipher.encrypt(testBytes);
        assertThat(encryptedBytes, IsNot.not(IsEqual.equalTo(testBytes)));
        assertFalse(encryptedBytes.length == 0);

        byte[] decryptedBytes = cipher.decrypt(encryptedBytes);
        assertArrayEquals(testBytes, decryptedBytes);
    }
}
