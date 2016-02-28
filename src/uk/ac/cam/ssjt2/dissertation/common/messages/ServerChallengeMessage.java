package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Spencer on 5/11/2015.
 */
public class ServerChallengeMessage extends Message {

    private final int ServerNonce;
    private final int ClientId;
    private final String SessionId;

    public ServerChallengeMessage(int serverNonce, int clientId, String sessionId) throws IOException {
        super(AuthenticationProtocol.HEADER_SERVER_CHALLENGE);
        ServerNonce = serverNonce;
        ClientId = clientId;
        SessionId = sessionId;
    }

    public int getServerNonce() {
        return ServerNonce;
    }

    public int getClientId() {
        return ClientId;
    }

    public String getSessionId() {
        return SessionId;
    }

}
