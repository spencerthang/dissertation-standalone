package uk.ac.cam.ssjt2.dissertation.client;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.MessageHandlerBase;
import uk.ac.cam.ssjt2.dissertation.common.messages.ServerChallengeMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.ServerChallengeResponseMessage;

import java.io.*;

/**
 * Created by Spencer on 5/11/2015.
 */
public class ClientServerMessageHandler extends MessageHandlerBase {

    private final AuthenticationClient m_Client;

    public ClientServerMessageHandler(InputStream inputStream, OutputStream outputStream, AuthenticationClient client) {
        super(inputStream, outputStream);
        m_Client = client;
        m_SessionKey = m_Client.getSessionKey();
    }

    @Override
    public void processMessage(InputStream inputStream, byte header) throws IOException {
        log("Received packet header: " + header);

        switch(header) {
            case AuthenticationProtocol.HEADER_TEST:
                log("Received test message.");
                break;
            case AuthenticationProtocol.HEADER_SERVER_CHALLENGE:
                log("Received server handshake challenge.");
                int nonce = ServerChallengeMessage.readFromStream(inputStream);
                log("Decoded challenge nonce: " + nonce);
                sendEncrypted(m_OutputStream, new ServerChallengeResponseMessage(nonce - 1).getBytes());
                break;
            default:
                logError("Unrecognized message header: " + header);
        }
    }

    public void sendEncrypted(OutputStream outputStream, byte[] message) throws IOException {
        DataOutputStream dos = new DataOutputStream(outputStream);
        // Encrypt into byte array
        CipherTools clientCipher = null;
        try {
            clientCipher = new CipherTools(m_SessionKey);
            byte[] encrypted = clientCipher.encrypt(message);
            dos.write(AuthenticationProtocol.HEADER_SESSION_ENCRYPTED);
            dos.writeInt(encrypted.length);
            dos.write(encrypted);
            dos.flush();
        } catch (Exception e) {
            logError("Failed to send session encrypted message.");
            e.printStackTrace();
            return;
        }
    }

    public void log(String message) {
        System.out.println("[Client " + m_Client.getClientId() + "] " + message);
    }

    public void logError(String message) {
        System.out.println("[Client " + m_Client.getClientId() + "] " + message);
    }
}