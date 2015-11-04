package uk.ac.cam.ssjt2.dissertation.tests;

import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.client.AuthenticationClient;
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
    public void canPerformRoundTripKDCResponseMessage() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IOException {
        KDCResponseMessage writeKDC = new KDCResponseMessage();
        int clientId = 9;
        SecretKey clientKey = CipherTools.GenerateSecretKey();
        int clientNonce = 19;
        int targetId = 35;
        SecretKey targetKey = CipherTools.GenerateSecretKey();
        SecretKey sessionKey = CipherTools.GenerateSecretKey();

        writeKDC.encrypt(clientId, clientKey, clientNonce, targetId, targetKey, sessionKey);
        byte[] encryptedBytes = writeKDC.getBytes();

        assertEquals(AuthenticationProtocol.HEADER_KDC_RESPONSE, encryptedBytes[0]);

        byte[] encryptedBytesWithoutHeader = Arrays.copyOfRange(encryptedBytes, 1, encryptedBytes.length);

        try(ByteArrayInputStream bis = new ByteArrayInputStream(encryptedBytesWithoutHeader)) {
            KDCResponseMessage readKDC = (KDCResponseMessage) new KDCResponseMessage().readFromStream(bis);
            readKDC.decrypt(clientKey, clientNonce, targetId);
            assertEquals(clientNonce, readKDC.getClientNonce());
            assertEquals(targetId, readKDC.getTargetId());
        }
    }
}
