package uk.ac.cam.ssjt2.dissertation.client;

import uk.ac.cam.ssjt2.dissertation.common.messages.KDCRequestMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.ServerHandshakeMessage;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

/**
 * Created by Spencer on 1/11/2015.
 */
public class AuthenticationClient implements AutoCloseable {

    private final int m_ClientId;
    private final SecretKey m_ClientKey;

    private Socket m_KDCClient = null;
    private Socket m_ServerClient = null;

    private SecretKey m_SessionKey = null;
    private byte[] m_EncryptedMessageToServer;

    private int m_Nonce;
    private int m_TargetId;

    public AuthenticationClient(int clientId, SecretKey clientKey) {
        m_ClientId = clientId;
        m_ClientKey = clientKey;
    }

    public boolean startSessionKeyRetrieval(String kdcAddress, int kdcPort, int targetId) throws IOException {
        m_KDCClient = new Socket(kdcAddress, kdcPort);
        if(m_KDCClient != null && m_KDCClient.isConnected()) {
            // Start message handling
            ClientKDCMessageHandler kdcHandler = new ClientKDCMessageHandler(m_KDCClient.getInputStream(), m_KDCClient.getOutputStream(), this);
            Thread kdcHandlerThread = new Thread(kdcHandler);
            kdcHandlerThread.start();

            // Send KDC Request Message
            Random rand = new Random();
            m_Nonce = rand.nextInt();
            m_TargetId = targetId;
            KDCRequestMessage request = new KDCRequestMessage(m_ClientId, m_TargetId, m_Nonce);
            m_KDCClient.getOutputStream().write(request.getBytes());
            return true;
        } else {
            return false;
        }
    }

    public boolean connectToServer(String targetAddress, int serverPort) throws IOException {
        m_ServerClient = new Socket(targetAddress, serverPort);
        if(m_ServerClient != null && m_ServerClient.isConnected()) {
            // Start message handling
            ClientServerMessageHandler serverHandler = new ClientServerMessageHandler(m_ServerClient.getInputStream(), m_ServerClient.getOutputStream(), this);
            Thread serverHandlerThread = new Thread(serverHandler);
            serverHandlerThread.start();

            // Send Server Handshake Message
            ServerHandshakeMessage request = new ServerHandshakeMessage(m_EncryptedMessageToServer);
            m_ServerClient.getOutputStream().write(request.getBytes());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        if(m_KDCClient != null) {
            m_KDCClient.close();
        }
        if(m_ServerClient != null) {
            m_ServerClient.close();
        }
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
