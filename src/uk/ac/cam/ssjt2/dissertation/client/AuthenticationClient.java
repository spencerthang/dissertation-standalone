package uk.ac.cam.ssjt2.dissertation.client;

import com.google.gson.Gson;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.Message;
import uk.ac.cam.ssjt2.dissertation.common.messages.KDCRequestMessage;

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
    private final SecretKey m_ClientKey;

    private SecretKey m_SessionKey = null;
    private byte[] m_EncryptedMessageToServer;

    private int m_Nonce;
    private int m_TargetId;

    public AuthenticationClient(int clientId, SecretKey clientKey) {
        m_ClientId = clientId;
        m_ClientKey = clientKey;
    }

    public void startSessionKeyRetrieval(String kdcUrl, int targetId) throws IOException, NoSuchAlgorithmException {
        // Craft new KDC Request Message
        HttpClient kdcClient = new HttpClient(kdcUrl);
        Random rand = new Random();
        m_Nonce = rand.nextInt();
        m_TargetId = targetId;
        KDCRequestMessage kdcRequestMessage = new KDCRequestMessage(m_ClientId, m_TargetId, m_Nonce);

        // Send KDC Request Message
        String kdcResponse = kdcClient.post(kdcRequestMessage.getJson());
        System.out.println(kdcResponse);
        System.out.println(Message.fromEncryptedResponse(m_ClientKey, kdcResponse).getJson());
    }

    public boolean connectToServer(String targetAddress, int serverPort) throws IOException {
        return false;
    }

    protected SecretKey getClientKey() {
        return m_ClientKey;
    }

    protected int getNonce() {
        return m_Nonce;
    }

    protected int getTargetId() {
        return m_TargetId;
    }

    protected int getClientId() {
        return m_ClientId;
    }

    public boolean hasSessionKey() {
        return m_SessionKey != null;
    }

    protected void setSessionKey(SecretKey sessionKey) {
        m_SessionKey = sessionKey;
    }

    protected SecretKey getSessionKey() {
        return m_SessionKey;
    }

    public byte[] getEncryptedMessageToServer() {
        return m_EncryptedMessageToServer;
    }

    public void setEncryptedMessageToServer(byte[] encryptedMessageToServer) {
        m_EncryptedMessageToServer = encryptedMessageToServer;
    }
}
