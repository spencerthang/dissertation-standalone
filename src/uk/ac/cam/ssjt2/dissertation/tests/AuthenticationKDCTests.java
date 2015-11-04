package uk.ac.cam.ssjt2.dissertation.tests;

import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.messages.KDCResponseMessage;
import uk.ac.cam.ssjt2.dissertation.kdc.AuthenticationKDC;

import javax.crypto.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by Spencer on 4/11/2015.
 */
public class AuthenticationKDCTests {

    public static final String c_ServerAddress = "127.0.0.1";
    public static int m_ServerPort = 5000;
    public static final int c_ClientId = 9;
    public static final int c_ClientNonce = 19;
    public static final int c_TargetId = 35;

    private int getServerPort() {
        do {
            m_ServerPort++;
            if(isPortAvailable(m_ServerPort)) {
                return m_ServerPort;
            }
        } while (m_ServerPort < 5100);
        throw new IllegalArgumentException("Failed to open any ports from 5000 - 5100");
    }

    private boolean isPortAvailable(int port) {
        try (Socket ignored = new Socket(c_ServerAddress, port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }

    @Test
    public void canCreateNewKDC() {
        // Create test server
        int serverPort = getServerPort();
        AuthenticationKDC testServer = new AuthenticationKDC(serverPort);
        Thread serverThread = new Thread(testServer);
        serverThread.start();

        assertFalse(isPortAvailable(serverPort));
    }

    @Test

    @Test
    public void canPerformRoundTripKDCResponseMessage() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IOException {
        KDCResponseMessage writeKDC = new KDCResponseMessage();
        SecretKey clientKey = CipherTools.GenerateSecretKey();
        SecretKey targetKey = CipherTools.GenerateSecretKey();
        SecretKey sessionKey = CipherTools.GenerateSecretKey();

        writeKDC.encrypt(c_ClientId, clientKey, c_TargetId, targetKey, c_ClientNonce, sessionKey);
        byte[] encryptedBytes = writeKDC.getBytes();

        assertEquals(AuthenticationProtocol.HEADER_KDC_RESPONSE, encryptedBytes[0]);

        byte[] encryptedBytesWithoutHeader = Arrays.copyOfRange(encryptedBytes, 1, encryptedBytes.length);

        try(ByteArrayInputStream bis = new ByteArrayInputStream(encryptedBytesWithoutHeader)) {
            KDCResponseMessage.KDCResponse readKDC = KDCResponseMessage.readFromStream(bis, clientKey);
            assertEquals(c_ClientNonce, readKDC.getClientNonce());
            assertEquals(c_TargetId, readKDC.getTargetId());
        }
    }
}
