package uk.ac.cam.ssjt2.dissertation.common.messages;

import com.sun.prism.PixelFormat;
import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.Message;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Spencer on 4/11/2015.
 */
public class KDCResponseMessage extends Message {

    private final int ClientNonce;
    private final int TargetId;
    private final String TargetIV;
    private final String SessionKey;
    private final String TargetMessage;

    public KDCResponseMessage(byte header, int clientNonce, int targetId, String targetIV, String sessionKey, String targetMessage) {
        super(AuthenticationProtocol.HEADER_KDC_RESPONSE);
        ClientNonce = clientNonce;
        TargetId = targetId;
        TargetIV = targetIV;
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
        return new SecretKeySpec(DatatypeConverter.parseBase64Binary(SessionKey), CipherTools.CipherTransformation);
    }

    public byte[] getTargetIv() {
        return DatatypeConverter.parseBase64Binary(TargetIV);
    }

}
