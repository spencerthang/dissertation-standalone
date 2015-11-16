package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.MessageBase;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Spencer on 5/11/2015.
 */
public class ServerChallengeResponseMessage extends MessageBase {

    private final int m_Nonce;

    public ServerChallengeResponseMessage(int nonce) throws IOException {
        super(AuthenticationProtocol.HEADER_SERVER_CHALLENGE_RESPONSE);
        m_Nonce = nonce;
    }

    public static int readFromStream(InputStream inputStream) throws IOException {
        DataInputStream dis = new DataInputStream(inputStream);
        return dis.readInt();
    }

}
