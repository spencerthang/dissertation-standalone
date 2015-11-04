package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.MessageBase;

import javax.crypto.*;
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
    private byte[] m_EncryptedBytes;

    public KDCResponseMessage() {
        super(AuthenticationProtocol.HEADER_KDC_RESPONSE);
    }

    public void encrypt(int clientId, SecretKey clientKey, int clientNonce, int targetId, SecretKey targetKey, SecretKey sessionKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
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

        try(DataOutputStream daos = new DataOutputStream(m_Buffer)) {
            daos.writeInt(clientEncryptedMessage.length);
            daos.flush();
        }

        m_Buffer.write(clientEncryptedMessage);
    }

    public void decrypt(SecretKey clientKey, int clientNonce, int targetId) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        if(m_EncryptedBytes == null)
            throw new IllegalArgumentException("KDC response message has nothing to decrypt.");

        CipherTools clientCipher = new CipherTools(clientKey);
        byte[] decrypted = clientCipher.decrypt(m_EncryptedBytes);

        try(DataInputStream dis = new DataInputStream(new ByteArrayInputStream(decrypted))) {
            int sessionKeyLength = dis.readInt();
            byte[] sessionKeyBytes = new byte[sessionKeyLength];
            dis.readFully(sessionKeyBytes);
            m_SessionKey = new SecretKeySpec(sessionKeyBytes, 0, sessionKeyBytes.length, CipherTools.CipherTransformation);

            m_TargetId = dis.readInt();
            m_ClientNonce = dis.readInt();

            int targetEncryptedMessageLength = dis.readInt();
            byte[] targetEncryptedMessageBytes = new byte[targetEncryptedMessageLength];
            dis.readFully(targetEncryptedMessageBytes);
        }
    }

    @Override
    public MessageBase readFromStream(InputStream inputStream) throws IOException {
        try(DataInputStream dis = new DataInputStream(inputStream)) {
            int messageLength = dis.readInt();
            byte[] encryptedMessage = new byte[messageLength];
            dis.readFully(encryptedMessage);
            m_EncryptedBytes = encryptedMessage;
        }
        return this;
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
