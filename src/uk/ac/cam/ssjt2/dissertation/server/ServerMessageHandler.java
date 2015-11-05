package uk.ac.cam.ssjt2.dissertation.server;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.HexTools;
import uk.ac.cam.ssjt2.dissertation.common.MessageHandlerBase;
import uk.ac.cam.ssjt2.dissertation.common.messages.ServerChallengeMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.ServerChallengeResponseMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.ServerHandshakeMessage;

import javax.crypto.SecretKey;
import java.io.*;
import java.util.Random;

/**
 * Created by Spencer on 4/11/2015.
 */
public class ServerMessageHandler extends MessageHandlerBase {

    private final AuthenticationServer m_Server;
    private int m_ClientId;
    private boolean m_Authenticated = false;
    private Integer m_Nonce;

    public ServerMessageHandler(InputStream inputStream, OutputStream outputStream, AuthenticationServer server) {
        super(inputStream, outputStream);
        m_Server = server;
    }

    @Override
    public void processMessage(InputStream inputStream, byte header) throws IOException {
        log("Received packet header: " + header);

        switch (header) {
            case AuthenticationProtocol.HEADER_TEST:
                log("Received test message.");
                break;
            case AuthenticationProtocol.HEADER_SERVER_HANDSHAKE:
                log("Received server handshake message.");
                try {
                    ServerHandshakeMessage.ServerHandshakeResult result = ServerHandshakeMessage.readHandshakeFromStream(inputStream, m_Server.getServerKey());
                    m_ClientId = result.getClientId();
                    m_SessionKey = result.getSessionKey();
                    log("Handshake from client " + result.getClientId() + " decoded, session key: " + HexTools.bytesToHex(m_SessionKey.getEncoded()));

                    Random rand = new Random();
                    m_Nonce = rand.nextInt();
                    writeEncrypted(m_OutputStream, new ServerChallengeMessage(m_Nonce).getBytes());
                    log("Sent challenge with nonce " + m_Nonce);
                } catch (Exception e) {
                    logError("Error occurred while trying to decode server handshake.");
                    e.printStackTrace();
                }
                break;
            case AuthenticationProtocol.HEADER_SERVER_CHALLENGE_RESPONSE:
                log("Received server handshake challenge response.");
                int nonce = ServerChallengeResponseMessage.readFromStream(inputStream);
                log("Decoded challenge response nonce: " + nonce);

                if (m_Nonce != null && nonce == m_Nonce - 1) {
                    m_Authenticated = true;
                    log("Nonce accepted, client is now authenticated.");
                } else {
                    logError("None rejected, client is not authenticated.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unrecognized message header: " + header);
        }
    }

    @Override
    protected String getLogTag() {
        return "[Server " + m_Server.getServerId() + "] ";
    }
}
