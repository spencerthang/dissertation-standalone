package uk.ac.cam.ssjt2.dissertation.tests;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.client.AuthenticationClient;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.kdc.AuthenticationKDC;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Spencer on 4/11/2015.
 */
public class IntegrationTests {

    public static final String c_KDCAddress = "127.0.0.1";
    public static int c_KDCPort = 5000;
    public static final int c_ClientId = 13;
    public static final int c_TargetId = 17;
    public final SecretKey c_ClientKey;
    public final SecretKey c_TargetKey;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    public IntegrationTests() throws NoSuchAlgorithmException {
        c_ClientKey = CipherTools.GenerateSecretKey();
        c_TargetKey = CipherTools.GenerateSecretKey();
        System.setOut(new PrintStream(outContent));
    }

    private int getServerPort() {
        c_KDCPort++;
        return c_KDCPort;
    }

    @Test
    public void canPerformClientKDCExchange() throws IOException, InterruptedException {
        // Create KDC server
        int serverPort = getServerPort();
        AuthenticationKDC kdcServer = new AuthenticationKDC(serverPort);
        kdcServer.addKey(c_ClientId, c_ClientKey);
        kdcServer.addKey(c_TargetId, c_TargetKey);
        Thread serverThread = new Thread(kdcServer);
        serverThread.start();

        // Create authentication client
        AuthenticationClient client = new AuthenticationClient(c_ClientId, c_ClientKey);
        client.startSessionKeyRetrieval(c_KDCAddress, serverPort, c_TargetId);

        // Wait for authentication to complete
        Thread.sleep(2000);

        String outContentString = getAndDisplayOutContent();
        assertThat(outContentString, CoreMatchers.containsString("KDC response decoded, target: " + c_TargetId));
    }

    private String getAndDisplayOutContent() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String ret = outContent.toString();
        System.out.println(ret);
        return ret;
    }

}
