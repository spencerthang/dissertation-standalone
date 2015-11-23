package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.Message;

import java.io.IOException;

/**
 * Created by Spencer on 5/11/2015.
 */
public class ServerHandshakeMessage extends Message {

    private final String Handshake;

    // Used by client to create a handshake message
    public ServerHandshakeMessage(String handshake) throws IOException {
        super(AuthenticationProtocol.HEADER_SERVER_HANDSHAKE);
        Handshake = handshake;
    }

}
