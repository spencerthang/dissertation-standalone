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

    // Used by client to create a handshake message
    public ServerHandshakeMessage(byte[] encryptedMessageToServer) throws IOException {
        super(AuthenticationProtocol.HEADER_SERVER_HANDSHAKE);
        //m_Buffer.writeInt(encryptedMessageToServer.length);
        //m_Buffer.write(encryptedMessageToServer);
    }

    // Used by server to decode the handshake message
    public static ServerHandshakeResult readHandshakeFromStream(InputStream inputStream, SecretKey serverKey) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        DataInputStream dis = new DataInputStream(inputStream);
        int messageLength = dis.readInt();
        byte[] encryptedMessage = new byte[messageLength];
        dis.readFully(encryptedMessage);

        CipherTools serverCipher = new CipherTools(serverKey, CipherTools.GenerateIV());
        byte[] decrypted = serverCipher.decrypt(encryptedMessage);

        try(DataInputStream decryptedDis = new DataInputStream(new ByteArrayInputStream(decrypted))) {
            int sessionKeyLength = decryptedDis.readInt();
            byte[] sessionKeyBytes = new byte[sessionKeyLength];
            decryptedDis.readFully(sessionKeyBytes);
            SecretKey sessionKey = new SecretKeySpec(sessionKeyBytes, 0, sessionKeyBytes.length, CipherTools.CipherTransformation);
            int clientId = decryptedDis.readInt();
            return new ServerHandshakeResult(clientId, sessionKey);
        }
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
