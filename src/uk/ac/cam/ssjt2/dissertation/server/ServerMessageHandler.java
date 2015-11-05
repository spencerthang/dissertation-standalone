package uk.ac.cam.ssjt2.dissertation.server;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.MessageHandlerBase;
import uk.ac.cam.ssjt2.dissertation.common.messages.ServerHandshakeMessage;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Spencer on 4/11/2015.
 */
public class ServerMessageHandler extends MessageHandlerBase {

    private final AuthenticationServer m_Server;
    private int m_ClientId;
    private SecretKey m_SessionKey;
    private boolean m_Authenticated = false;

    public ServerMessageHandler(InputStream inputStream, OutputStream outputStream, AuthenticationServer server) {
        super(inputStream, outputStream);
        m_Server = server;
    }

    @Override
    public void handleMessage() throws IOException {
        byte header = (byte) m_InputStream.read();
        log("Received packet header: " + header);

        switch(header) {
            case AuthenticationProtocol.HEADER_TEST:
                log("Received test message.");
                break;
            case AuthenticationProtocol.HEADER_SERVER_HANDSHAKE:
                log("Received server handshake message.");
                try {
                    ServerHandshakeMessage.ServerHandshakeResult result = ServerHandshakeMessage.readHandshakeFromStream(m_InputStream, m_Server.getServerKey());
                    m_ClientId = result.getClientId();
                    m_SessionKey = result.getSessionKey();
                    log("Handshake from client " + result.getClientId() + " decoded.");
                } catch (Exception e) {
                    logError("Error occurred while trying to decode server handshake.");
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalArgumentException("Unrecognized message header: " + header);
        }
    }

    public void log(String message) {
        System.out.println("[Server " + m_Server.getServerId() + "] " + message);
    }

    public void logError(String message) {
        System.out.println("[Server " + m_Server.getServerId() + "] " + message);
    }

}
