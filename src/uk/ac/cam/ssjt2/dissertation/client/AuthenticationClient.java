package uk.ac.cam.ssjt2.dissertation.client;

import uk.ac.cam.ssjt2.dissertation.common.Message;
import uk.ac.cam.ssjt2.dissertation.common.exceptions.InvalidNonceException;
import uk.ac.cam.ssjt2.dissertation.common.exceptions.InvalidTargetException;
import uk.ac.cam.ssjt2.dissertation.common.exceptions.SymmetricProtocolException;
import uk.ac.cam.ssjt2.dissertation.common.messages.*;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Random;

/**
 * Created by Spencer on 1/11/2015.
 */
public class AuthenticationClient {

    private final int m_ClientId;

    private final int m_TargetId;
    private final SecretKey m_ClientKey;

    private SecretKey m_SessionKey = null;
    private String m_SessionId;
    private String m_KDCMessageToServer;
    private HttpClient m_ServerClient = null;
    private boolean m_Authenticated = false;

    public AuthenticationClient(int clientId, int targetId, SecretKey clientKey) {
        m_ClientId = clientId;
        m_TargetId = targetId;
        m_ClientKey = clientKey;
    }

    public void retrieveSessionKey(String kdcUrl) throws IOException, SymmetricProtocolException {
        // Craft new KDC Request Message
        HttpClient kdcClient = new HttpClient(kdcUrl);
        Random rand = new Random();
        int clientNonce = rand.nextInt();
        KDCRequestMessage kdcRequestMessage = new KDCRequestMessage(m_ClientId, m_TargetId, clientNonce);

        // Request KDC Response
        String encryptedKDCResponse = kdcClient.post(new PostContents(kdcRequestMessage));
        KDCResponseMessage kdcResponse = ((KDCResponseMessage)Message.fromEncryptedResponse(m_ClientKey, encryptedKDCResponse));

        // Validate KDC Response
        if(kdcResponse.getClientNonce() != clientNonce) {
            throw new InvalidNonceException("Expected: " + clientNonce + " Actual: " + kdcResponse.getClientNonce());
        } else if(kdcResponse.getTargetId() != m_TargetId) {
            throw new InvalidTargetException("Expected: " + m_TargetId + " Actual: " + kdcResponse.getTargetId());
        }

        m_SessionKey = kdcResponse.getSessionKey();
        m_KDCMessageToServer = kdcResponse.getTargetMessage();

        System.out.println("KDC Response validated, nonce: " + kdcResponse.getClientNonce() + ", target: " + kdcResponse.getTargetId());
    }

    public boolean connectToServer(String targetUrl) throws IOException, SymmetricProtocolException {
        if(m_SessionKey == null || m_KDCMessageToServer == null) {
            throw new SymmetricProtocolException("Cannot connect to server without KDC token");
        }

        m_ServerClient = new HttpClient(targetUrl);
        ServerHandshakeMessage handshakeMessage = new ServerHandshakeMessage(m_KDCMessageToServer);

        // Make Server Handshake
        String encryptedServerChallenge = m_ServerClient.post(new PostContents(handshakeMessage));
        ServerChallengeMessage serverChallenge = ((ServerChallengeMessage)Message.fromEncryptedResponse(m_SessionKey, encryptedServerChallenge));
        m_SessionId = serverChallenge.getSessionId();

        System.out.println("Server assigned session id: " + m_SessionId);

        // Respond to Server Challenge
        ServerChallengeResponseMessage serverChallengeResponseMessage = new ServerChallengeResponseMessage(serverChallenge.getServerNonce());
        String encryptedServerAuthentication = sendEncryptedMessage(serverChallengeResponseMessage);
        ServerAuthenticationStatusMessage serverAuthentication = ((ServerAuthenticationStatusMessage)Message.fromEncryptedResponse(m_SessionKey, encryptedServerAuthentication));

        System.out.println("Server authentication: " + serverAuthentication.isAuthenticated() + " with server nonce " + serverChallenge.getServerNonce());

        // Check if we are authenticated
        m_Authenticated = serverAuthentication.isAuthenticated();
        return m_Authenticated;
    }

    public String sendUserMessage(UserMessage send) throws IOException, SymmetricProtocolException {
        String encryptedResponse = sendEncryptedMessage(send);
        UserMessageResponse userMessageResponse = ((UserMessageResponse)Message.fromEncryptedResponse(m_SessionKey, encryptedResponse));
        return userMessageResponse.getResponse();
    }

    private String sendEncryptedMessage(Message send) throws IOException, SymmetricProtocolException {
        try {
            return m_ServerClient.post(new EncryptedPostContents(send, m_SessionKey, m_SessionId));
        } catch (GeneralSecurityException e) {
            throw new SymmetricProtocolException(e);
        }
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

    public boolean isAuthenticated() {
        return m_ServerClient != null && m_Authenticated;
    }
}
