package uk.ac.cam.ssjt2.dissertation.tests;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.client.AuthenticationClient;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.kdc.AuthenticationKDC;
import uk.ac.cam.ssjt2.dissertation.server.AuthenticationServer;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

/**
 * Created by Spencer on 4/11/2015.
 */
public class IntegrationTests {

    public static final String c_ServerAddress = "127.0.0.1";
    public static final String c_KDCAddress = "127.0.0.1";
    public static int c_KDCPort = 5000;
    public static final int c_ClientId = 13;
    public static final int c_TargetId = 17;
    public final SecretKey c_ClientKey;
    public final SecretKey c_TargetKey;

    public IntegrationTests() throws NoSuchAlgorithmException {
        c_ClientKey = CipherTools.GenerateSecretKey();
        c_TargetKey = CipherTools.GenerateSecretKey();
    }

    private int getServerPort() {
        c_KDCPort++;
        return c_KDCPort;
    }

    @Test
    public void canPerformClientKDCExchange() throws IOException, InterruptedException {
        // Create KDC server
        int kdcPort = createKDCServer();

        // Create authentication client
        AuthenticationClient client = new AuthenticationClient(c_ClientId, c_ClientKey);
        client.startSessionKeyRetrieval(c_KDCAddress, kdcPort, c_TargetId);

        // Wait for authentication to complete
        Thread.sleep(2000);

        // Verify client has session key
        assertTrue(client.hasSessionKey());
    }

    @Test
    public void canPerformClientServerConnection() throws IOException, InterruptedException {
        // Create KDC server
        int kdcPort = createKDCServer();

        // Create target server
        int targetPort = createTargetServer();

        // Create authentication client
        AuthenticationClient client = new AuthenticationClient(c_ClientId, c_ClientKey);
        client.startSessionKeyRetrieval(c_KDCAddress, kdcPort, c_TargetId);

        // Wait for authentication to complete
        Thread.sleep(2000);

        assertTrue(client.hasSessionKey());
        assertTrue(client.getEncryptedMessageToServer() != null);

        client.connectToServer(c_ServerAddress, targetPort);
    }

    private int createKDCServer() {
        int serverPort = getServerPort();
        AuthenticationKDC kdcServer = new AuthenticationKDC(serverPort);
        kdcServer.addKey(c_ClientId, c_ClientKey);
        kdcServer.addKey(c_TargetId, c_TargetKey);
        Thread serverThread = new Thread(kdcServer);
        serverThread.start();
        return serverPort;
    }

    private int createTargetServer() {
        int serverPort = getServerPort();
        AuthenticationServer testServer = new AuthenticationServer(serverPort, c_TargetId, c_TargetKey);
        Thread serverThread = new Thread(testServer);
        serverThread.start();
        return serverPort;
    }

}
