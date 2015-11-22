package uk.ac.cam.ssjt2.dissertation.client;

import com.google.gson.Gson;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
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
    private IvParameterSpec m_ClientKdcIV;

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

        // Extract IV and Encrypted Message
        Gson gson = new Gson();
        KDCResponse response = gson.fromJson(kdcResponse, KDCResponse.class);
        byte[] iv = response.getIV();
        m_ClientKdcIV = new IvParameterSpec(response.getIV());

        // Decrypt KDC Response
        // Decrypt into byte array
        CipherTools clientCipher = null;
        try {
            clientCipher = new CipherTools(m_ClientKey, m_ClientKdcIV);
            byte[] data = response.getData();
            String decrypted = new String(clientCipher.decrypt(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class KDCResponse {
        String Data;
        String IV;

        public byte[] getData() {
            return DatatypeConverter.parseBase64Binary(Data);
        }

        public byte[] getIV() {
            return DatatypeConverter.parseBase64Binary(IV);
        }
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
