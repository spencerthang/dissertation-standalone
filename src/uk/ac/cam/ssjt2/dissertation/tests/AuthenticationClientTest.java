package uk.ac.cam.ssjt2.dissertation.tests;

import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.client.AuthenticationClient;
import uk.ac.cam.ssjt2.dissertation.common.messages.TestMessage;

import java.io.IOException;

import static org.junit.Assert.*;

public class AuthenticationClientTest {

    public static final String c_ServerAddress = "127.0.0.1";
    public static final int c_ServerPort = 5000;

    @Test
    public void canCreateNewClient() {
        AuthenticationClient client = new AuthenticationClient(c_ServerAddress, c_ServerPort);
        assertNotNull(client);
    }

    @Test
    public void canOpenConnection() throws IOException {
        // Create test server
        Thread serverThread = new Thread(new TestServer(c_ServerPort, null));
        serverThread.start();

        // Check connection succeeds
        AuthenticationClient client = new AuthenticationClient(c_ServerAddress, c_ServerPort);
        assertTrue(client.connect());
    }

    @Test
    public void canSendTestMessage() throws IOException, InterruptedException {
        // Create test server
        TestServer testServer = new TestServer(c_ServerPort, null);
        Thread serverThread = new Thread(testServer);
        serverThread.start();

        // Send test message
        AuthenticationClient client = new AuthenticationClient(c_ServerAddress, c_ServerPort);
        client.connect();
        client.sendMessage(new TestMessage());

        // Allow 1 second for processing
        Thread.sleep(1000);
        assertTrue(testServer.Messages.size() == 1);
        assertTrue(testServer.Messages.get(0) instanceof TestMessage);
    }


}