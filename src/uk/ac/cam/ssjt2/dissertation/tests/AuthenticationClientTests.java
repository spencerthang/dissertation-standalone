package uk.ac.cam.ssjt2.dissertation.tests;

import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.client.AuthenticationClient;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertNotNull;

public class AuthenticationClientTests {

    public static final String c_KdcUrl = "http://localhost/pico/kdc.php";
    public static final int c_ClientId = 1;
    public static final int c_TargetId = 2;
    public final SecretKey m_ClientKey;

    public AuthenticationClientTests() throws NoSuchAlgorithmException {
        m_ClientKey = new SecretKeySpec(DatatypeConverter.parseBase64Binary("eCd2T3UxOG8WfbuTm2DxiQ=="), "AES");
    }

    @Test
    public void canCreateNewClient() {
        AuthenticationClient client = new AuthenticationClient(c_ClientId, m_ClientKey);
        assertNotNull(client);
    }

    @Test
    public void canPerformSessionKeyRetrieval() throws IOException, NoSuchAlgorithmException {
        AuthenticationClient client = new AuthenticationClient(c_ClientId, m_ClientKey);
        client.startSessionKeyRetrieval(c_KdcUrl, c_TargetId);
        assertNotNull(client);
    }


}