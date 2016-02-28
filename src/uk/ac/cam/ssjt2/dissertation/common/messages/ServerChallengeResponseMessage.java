package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Spencer on 5/11/2015.
 */
public class ServerChallengeResponseMessage extends Message {

    private final int ServerNonce;

    public ServerChallengeResponseMessage(int nonce) throws IOException {
        super(AuthenticationProtocol.HEADER_SERVER_CHALLENGE_RESPONSE);
        ServerNonce = nonce;
    }

}
