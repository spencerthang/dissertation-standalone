package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.Message;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

    public static class ServerHandshakeResult {
        private final int m_ClientId;
        private final SecretKey m_SessionKey;

        public ServerHandshakeResult(int clientId, SecretKey sessionKey) {
            m_ClientId = clientId;
            m_SessionKey = sessionKey;
        }

        public int getClientId() {
            return m_ClientId;
        }

        public SecretKey getSessionKey() {
            return m_SessionKey;
        }
    }

}
