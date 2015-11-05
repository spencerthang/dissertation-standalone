package uk.ac.cam.ssjt2.dissertation.tests;

import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.messages.KDCResponseMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.ServerHandshakeMessage;
import uk.ac.cam.ssjt2.dissertation.server.AuthenticationServer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Spencer on 5/11/2015.
 */
public class AuthenticationServerTests {

    public static final String c_ServerAddress = "127.0.0.1";
    public static int m_ServerPort = 5000;
    public static final int c_ClientId = 9;
    public static final int c_ClientNonce = 19;
    public static final int c_TargetId = 35;
    public final SecretKey c_TargetKey;

    public AuthenticationServerTests() throws NoSuchAlgorithmException {
        c_TargetKey = CipherTools.GenerateSecretKey();
    }

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
    public void canCreateNewServer() {
        // Create test server
        int serverPort = getServerPort();
        AuthenticationServer testServer = new AuthenticationServer(serverPort, c_TargetId, c_TargetKey);
        Thread serverThread = new Thread(testServer);
        serverThread.start();

        assertFalse(isPortAvailable(serverPort));
    }

    @Test
    public void canPerformRoundTripServerHandshakeMessage() throws IOException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        SecretKey clientKey = CipherTools.GenerateSecretKey();
        SecretKey targetKey = CipherTools.GenerateSecretKey();
        SecretKey sessionKey = CipherTools.GenerateSecretKey();

        KDCResponseMessage writeKDC = new KDCResponseMessage(c_ClientId, clientKey, c_TargetId, targetKey, c_ClientNonce, sessionKey);
        byte[] encryptedBytes = writeKDC.getBytes();

        byte[] encryptedBytesWithoutHeader = Arrays.copyOfRange(encryptedBytes, 1, encryptedBytes.length);
        byte[] encryptedMessageToTarget;
        try(ByteArrayInputStream bis = new ByteArrayInputStream(encryptedBytesWithoutHeader)) {
            KDCResponseMessage.KDCResponse readKDC = KDCResponseMessage.readFromStream(bis, clientKey);
            encryptedMessageToTarget = readKDC.getTargetEncryptedMessage();
        }

        ServerHandshakeMessage writeHandshake = new ServerHandshakeMessage(encryptedMessageToTarget);
        byte[] handshakeBytes = writeHandshake.getBytes();

        assertEquals(AuthenticationProtocol.HEADER_SERVER_HANDSHAKE, handshakeBytes[0]);

        byte[] handshakeBytesWithoutHeader = Arrays.copyOfRange(handshakeBytes, 1, handshakeBytes.length);
        try(ByteArrayInputStream bis = new ByteArrayInputStream(handshakeBytesWithoutHeader)) {
            ServerHandshakeMessage.ServerHandshakeResult result = ServerHandshakeMessage.readHandshakeFromStream(bis, targetKey);
            assertEquals(c_ClientId, result.getClientId());
            assertEquals(sessionKey, result.getSessionKey());
        }
    }
}
