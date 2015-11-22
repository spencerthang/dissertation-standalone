package uk.ac.cam.ssjt2.dissertation.client;

import com.google.gson.Gson;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.Message;
import uk.ac.cam.ssjt2.dissertation.common.exceptions.InvalidNonceException;
import uk.ac.cam.ssjt2.dissertation.common.exceptions.InvalidTargetException;
import uk.ac.cam.ssjt2.dissertation.common.exceptions.KDCException;
import uk.ac.cam.ssjt2.dissertation.common.messages.KDCRequestMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.KDCResponseMessage;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by Spencer on 1/11/2015.
 */
public class AuthenticationClient {

    private final int m_ClientId;
    private final int m_TargetId;
    private final SecretKey m_ClientKey;

    private SecretKey m_SessionKey = null;
    private String m_KDCMessageToServer;

    public AuthenticationClient(int clientId, int targetId, SecretKey clientKey) {
        m_ClientId = clientId;
        m_TargetId = targetId;
        m_ClientKey = clientKey;
    }

    public void retrieveSessionKey(String kdcUrl) throws IOException, NoSuchAlgorithmException, KDCException {
        // Craft new KDC Request Message
        HttpClient kdcClient = new HttpClient(kdcUrl);
        Random rand = new Random();
        int clientNonce = rand.nextInt();
        KDCRequestMessage kdcRequestMessage = new KDCRequestMessage(m_ClientId, m_TargetId, clientNonce);

        // Request KDC Response
        String encryptedKDCResponse = kdcClient.post(kdcRequestMessage.getJson());
        KDCResponseMessage kdcResponse = ((KDCResponseMessage)Message.fromEncryptedResponse(m_ClientKey, encryptedKDCResponse));

        // Validate KDC Response
        if(kdcResponse.getClientNonce() != clientNonce) {
            throw new InvalidNonceException("Expected: " + clientNonce + " Actual: " + kdcResponse.getClientNonce());
        } else if(kdcResponse.getTargetId() != m_TargetId) {
            throw new InvalidTargetException("Expected: " + m_TargetId + " Actual: " + kdcResponse.getTargetId());
        }

        m_SessionKey = kdcResponse.getSessionKey();
        m_KDCMessageToServer = kdcResponse.getTargetMessage();
    }

    public boolean connectToServer(String targetAddress, int serverPort) throws IOException {
        return false;
    }

    protected SecretKey getClientKey() {
        return m_ClientKey;
    }

    protected int getClientId() {
        return m_ClientId;
    }

    protected SecretKey getSessionKey() {
        return m_SessionKey;
    }
}
