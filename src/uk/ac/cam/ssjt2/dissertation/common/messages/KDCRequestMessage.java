package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.MessageBase;

import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;
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
    private final String ClientIV;

    public KDCRequestMessage(int clientId, int targetId, int clientNonce, IvParameterSpec clientIv) throws IOException {
        super(AuthenticationProtocol.HEADER_KDC_REQUEST);
        ClientId = clientId;
        TargetId = targetId;
        ClientNonce = clientNonce;
        ClientIV = DatatypeConverter.printBase64Binary(clientIv.getIV());
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
