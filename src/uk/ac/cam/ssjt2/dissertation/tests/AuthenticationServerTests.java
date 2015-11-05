package uk.ac.cam.ssjt2.dissertation.tests;

import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.kdc.AuthenticationKDC;
import uk.ac.cam.ssjt2.dissertation.server.AuthenticationServer;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.Socket;
import java.security.CryptoPrimitive;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertFalse;

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

}
