package uk.ac.cam.ssjt2.dissertation.tests;

import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.client.AuthenticationClient;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AuthenticationClientTests {

    public static final String c_ServerAddress = "127.0.0.1";
    public static int c_ServerPort = 5000;
    public static final int c_ClientId = 1;
    public final SecretKey m_ClientKey;

    public AuthenticationClientTests() throws NoSuchAlgorithmException {
        m_ClientKey = CipherTools.GenerateSecretKey();
    }

    private int getServerPort() {
        c_ServerPort++;
        return c_ServerPort;
    }

    @Test
    public void canCreateNewClient() {
        AuthenticationClient client = new AuthenticationClient(c_ClientId, m_ClientKey);
        assertNotNull(client);
    }


}