package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.MessageBase;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Spencer on 4/11/2015.
 */
public class KDCRequestMessage extends MessageBase {

    private final int ClientId;
    private final int TargetId;
    private final int ClientNonce;

    public KDCRequestMessage(int clientId, int targetId, int clientNonce) throws IOException {
        super(AuthenticationProtocol.HEADER_KDC_REQUEST);
        ClientId = clientId;
        TargetId = targetId;
        ClientNonce = clientNonce;
    }

    public static KDCRequestMessage readFromStream(InputStream inputStream) throws IOException {
        DataInputStream dis = new DataInputStream(inputStream);
        int clientId = dis.readInt();
        int targetId = dis.readInt();
        int clientNonce = dis.readInt();
        return new KDCRequestMessage(clientId, targetId, clientNonce);
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
