package uk.ac.cam.ssjt2.dissertation.tests;

import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.client.AuthenticationClient;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.exceptions.SymmetricProtocolException;
import uk.ac.cam.ssjt2.dissertation.common.messages.UserMessage;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class AuthenticationClientTests {

    public static final String c_KdcUrl = "http://localhost/pico/kdc.php";
    public static final String c_ServerUrl = "http://localhost/pico/server.php";
    public static final int c_ClientId = 1;
    public static final int c_TargetId = 2;
    public final SecretKey m_ClientKey;

    public AuthenticationClientTests() throws NoSuchAlgorithmException {
        m_ClientKey = new SecretKeySpec(DatatypeConverter.parseBase64Binary("eCd2T3UxOG8WfbuTm2DxiQ=="), CipherTools.CipherAlgorithm);
    }

    @Test
    public void canCreateNewClient() {
        AuthenticationClient client = new AuthenticationClient(c_ClientId, c_TargetId, m_ClientKey);
        assertNotNull(client);
    }

    @Test
    public void canPerformSessionKeyRetrieval() throws IOException, SymmetricProtocolException {
        AuthenticationClient client = new AuthenticationClient(c_ClientId, c_TargetId, m_ClientKey);
        client.retrieveSessionKey(c_KdcUrl);
        assertNotNull(client);
    }

    @Test
    public void canConnectToServer() throws IOException, SymmetricProtocolException {
        AuthenticationClient client = new AuthenticationClient(c_ClientId, c_TargetId, m_ClientKey);
        client.retrieveSessionKey(c_KdcUrl);
        assertTrue(client.connectToServer(c_ServerUrl));
        assertNotNull(client);
    }

    @Test
    public void canSendAddCommandToTestServer() throws IOException, SymmetricProtocolException {
        int x = 16, y = 30, expected = x + y;

        AuthenticationClient client = new AuthenticationClient(c_ClientId, c_TargetId, m_ClientKey);
        client.retrieveSessionKey(c_KdcUrl);
        assertTrue(client.connectToServer(c_ServerUrl));
        String response = client.sendUserMessage(new UserMessage("{\n" +
                "  \"cmd\": \"add\",\n" +
                "  \"x\": \"" + x + "\",\n" +
                "  \"y\": \"" + y + "\"\n" +
                "}"));

        System.out.println("Add x: " + x + ", y: " + y + ", expected: " + expected);
        System.out.println("Server response: " + response);
        assertEquals(Integer.parseInt(response), expected);
    }


}