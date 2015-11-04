package uk.ac.cam.ssjt2.dissertation.tests;

import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.AuthenticationClient;

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
    public void canTestConnection() throws IOException {
        // Create echo server
        String echoString = "123456";
        Thread echoServer = new Thread(new EchoServer(c_ServerPort, echoString));
        echoServer.start();

        // Get echo response
        AuthenticationClient client = new AuthenticationClient(c_ServerAddress, c_ServerPort);
        assertEquals(echoString, client.testConnection());
    }



}