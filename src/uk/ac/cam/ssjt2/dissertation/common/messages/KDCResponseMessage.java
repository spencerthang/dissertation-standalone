package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.Message;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * Created by Spencer on 4/11/2015.
 */
public class KDCResponseMessage extends Message {

    private final int ClientNonce;
    private final int TargetId;
    private final String SessionKey;
    private final String TargetMessage;

    public KDCResponseMessage(int clientNonce, int targetId, String sessionKey, String targetMessage) {
        super(AuthenticationProtocol.HEADER_KDC_RESPONSE);
        ClientNonce = clientNonce;
        TargetId = targetId;
        SessionKey = sessionKey;
        TargetMessage = targetMessage;
    }

    public int getClientNonce() {
        return ClientNonce;
    }

    public int getTargetId() {
        return TargetId;
    }

    public SecretKey getSessionKey() {
        return new SecretKeySpec(DatatypeConverter.parseBase64Binary(SessionKey), CipherTools.CipherAlgorithm);
    }

    public String getTargetMessage() {
        return TargetMessage;
    }
}
