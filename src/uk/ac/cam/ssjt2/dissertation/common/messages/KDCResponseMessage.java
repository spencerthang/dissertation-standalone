package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.MessageBase;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Spencer on 4/11/2015.
 */
public class KDCResponseMessage extends MessageBase {

    private int m_ClientNonce;
    private int m_TargetId;
    private SecretKey m_SessionKey;

    public KDCResponseMessage(int clientId, SecretKey clientKey, int targetId, SecretKey targetKey, int clientNonce, SecretKey sessionKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
        super(AuthenticationProtocol.HEADER_KDC_RESPONSE);
        CipherTools clientCipher = new CipherTools(clientKey);
        CipherTools targetCipher = new CipherTools(targetKey);
        byte[] sessionKeyBytes = sessionKey.getEncoded();

        byte[] targetEncryptedMessage;
        try(ByteArrayOutputStream unencryptedMessage = new ByteArrayOutputStream();
            DataOutputStream daos = new DataOutputStream(unencryptedMessage)) {
            daos.writeInt(sessionKeyBytes.length);
            daos.write(sessionKeyBytes);
            daos.writeInt(clientId);
            daos.flush();
            targetEncryptedMessage = targetCipher.encrypt(unencryptedMessage.toByteArray());
        }

        byte[] clientEncryptedMessage;
        try(ByteArrayOutputStream unencryptedMessage = new ByteArrayOutputStream();
            DataOutputStream daos = new DataOutputStream(unencryptedMessage)) {
            daos.writeInt(sessionKeyBytes.length);
            daos.write(sessionKeyBytes);
            daos.writeInt(targetId);
            daos.writeInt(clientNonce);
            daos.writeInt(targetEncryptedMessage.length);
            daos.write(targetEncryptedMessage);
            daos.flush();
            clientEncryptedMessage = clientCipher.encrypt(unencryptedMessage.toByteArray());
        }

        m_Buffer.writeInt(clientEncryptedMessage.length);
        m_Buffer.write(clientEncryptedMessage);
    }

    // This function decrypts and extract information from a KDC Response message.
    // This function does NOT verify the nonce or target ID.
    public static KDCResponse readFromStream(InputStream inputStream, SecretKey clientKey) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        DataInputStream dis = new DataInputStream(inputStream);
        int messageLength = dis.readInt();
        byte[] encryptedMessage = new byte[messageLength];
        dis.readFully(encryptedMessage);

        CipherTools clientCipher = new CipherTools(clientKey);
        byte[] decrypted = clientCipher.decrypt(encryptedMessage);

        try(DataInputStream decryptedDis = new DataInputStream(new ByteArrayInputStream(decrypted))) {
            int sessionKeyLength = decryptedDis.readInt();
            byte[] sessionKeyBytes = new byte[sessionKeyLength];
            decryptedDis.readFully(sessionKeyBytes);
            SecretKey sessionKey = new SecretKeySpec(sessionKeyBytes, 0, sessionKeyBytes.length, CipherTools.CipherTransformation);

            int responseTargetId = decryptedDis.readInt();
            int responseClientNonce = decryptedDis.readInt();

            int targetEncryptedMessageLength = decryptedDis.readInt();
            byte[] targetEncryptedMessageBytes = new byte[targetEncryptedMessageLength];
            decryptedDis.readFully(targetEncryptedMessageBytes);

            return new KDCResponse(responseTargetId, responseClientNonce, sessionKey, targetEncryptedMessageBytes);
        }
    }

    // Data structure to store useful information from a decoded KDC response message.
    public static class KDCResponse {
        private final int m_TargetId;
        private final int m_ClientNonce;
        private final SecretKey m_SessionKey;
        private final byte[] m_TargetEncryptedMessage;

        public KDCResponse(int targetId, int clientNonce, SecretKey sessionKey, byte[] targetEncryptedMessage) {
            m_TargetId = targetId;
            m_ClientNonce = clientNonce;
            m_SessionKey = sessionKey;
            m_TargetEncryptedMessage = targetEncryptedMessage;
        }

        public int getTargetId() {
            return m_TargetId;
        }

        public int getClientNonce() {
            return m_ClientNonce;
        }

        public SecretKey getSessionKey() {
            return m_SessionKey;
        }

        public byte[] getTargetEncryptedMessage() {
            return m_TargetEncryptedMessage;
        }
    }

    public int getClientNonce() {
        return m_ClientNonce;
    }

    public int getTargetId() {
        return m_TargetId;
    }

    public SecretKey getSessionKey() {
        return m_SessionKey;
    }
}
