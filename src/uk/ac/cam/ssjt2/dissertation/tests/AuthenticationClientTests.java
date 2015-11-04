package uk.ac.cam.ssjt2.dissertation.tests;

import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.client.AuthenticationClient;
import uk.ac.cam.ssjt2.dissertation.common.messages.TestMessage;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class AuthenticationClientTests {

    public static final String c_ServerAddress = "127.0.0.1";
    public static int m_ServerPort = 5000;
    public final SecretKey m_ClientKey;

    public AuthenticationClientTests() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        m_ClientKey = generator.generateKey();
    }

    private int getServerPort() {
        m_ServerPort++;
        return m_ServerPort;
    }

    @Test
    public void canCreateNewClient() {
        int serverPort = getServerPort();
        AuthenticationClient client = new AuthenticationClient(m_ClientKey);
        assertNotNull(client);
    }

    @Test
    public void canOpenConnection() throws IOException {
        // Create test server
        int serverPort = getServerPort();
        Thread serverThread = new Thread(new TestServer(serverPort, null));
        serverThread.start();

        // Check connection succeeds
        AuthenticationClient client = new AuthenticationClient(m_ClientKey);
        assertTrue(client.connect(c_ServerAddress, serverPort));
    }

    @Test
    public void canSendTestMessage() throws IOException, InterruptedException {
        // Create test server
        int serverPort = getServerPort();
        TestServer testServer = new TestServer(serverPort, null);
        Thread serverThread = new Thread(testServer);
        serverThread.start();

        // Send test message
        AuthenticationClient client = new AuthenticationClient(m_ClientKey);
        client.connect(c_ServerAddress, serverPort);
        client.sendMessage(new TestMessage());

        // Allow 1 second for processing
        Thread.sleep(1000);
        assertTrue(testServer.Messages.size() == 1);
        assertTrue(testServer.Messages.get(0) instanceof TestMessage);
    }

    @Test
    public void canReceiveTestMessage() throws IOException, InterruptedException {
        // Create test server
        int serverPort = getServerPort();
        TestServer testServer = new TestServer(serverPort, null);
        Thread serverThread = new Thread(testServer);
        serverThread.start();

        // Send test message
        AuthenticationClient client = new AuthenticationClient(m_ClientKey);
        client.connect(c_ServerAddress, serverPort);
        client.sendMessage(new TestMessage());

        // Await response, allow 2 seconds for processing
        Thread.sleep(2000);
        assertTrue(client.UnhandledMessages.size() == 1);
        assertTrue(client.UnhandledMessages.get(0) instanceof TestMessage);
    }


}