package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.Message;

import java.io.IOException;

/**
 * Created by Spencer on 4/11/2015.
 */
public class KDCRequestMessage extends Message {

    private final int ClientId;
    private final int TargetId;
    private final int ClientNonce;

    public KDCRequestMessage(int clientId, int targetId, int clientNonce) throws IOException {
        super(AuthenticationProtocol.HEADER_KDC_REQUEST);
        ClientId = clientId;
        TargetId = targetId;
        ClientNonce = clientNonce;
    }

    public int getClientId() {
        return ClientId;
    }

    public int getTargetId() {
        return TargetId;
    }

    public int getClientNonce() {
        return ClientNonce;
    }

}
