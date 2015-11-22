package uk.ac.cam.ssjt2.dissertation.common;

import javax.crypto.SecretKey;
import java.io.*;

/**
 * Created by Spencer on 3/11/2015.
 */
public abstract class MessageHandlerBase implements Runnable, AutoCloseable {

    protected final OutputStream m_OutputStream;
    protected final boolean m_LeaveOpen;
    protected SecretKey m_SessionKey = null;
    private final InputStream m_InputStream;

    public MessageHandlerBase(InputStream inputStream, OutputStream outputStream) {
        this(inputStream, outputStream, false);
    }

    public MessageHandlerBase(InputStream inputStream, OutputStream outputStream, boolean leaveOpen) {
        m_InputStream = new BufferedInputStream(inputStream);
        m_OutputStream = outputStream;
        m_LeaveOpen = leaveOpen;
    }

    @Override
    public void run() {
        while(m_InputStream != null) {
            try {
                byte header = (byte) m_InputStream.read();
                if(header == AuthenticationProtocol.HEADER_SESSION_ENCRYPTED) {
                    try(ByteArrayInputStream bis = new ByteArrayInputStream(readSessionEncryptedMessage())) {
                        processMessage(bis, (byte) bis.read());
                    }
                } else {
                    processMessage(m_InputStream, header);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    @Override
    public void close() throws Exception {
        if(m_InputStream != null && !m_LeaveOpen) {
            m_InputStream.close();
        }

        if(m_OutputStream != null && !m_LeaveOpen) {
            m_OutputStream.close();
        }
    }

    public abstract void processMessage(InputStream stream, byte header) throws IOException;

    private byte[] readSessionEncryptedMessage() throws IOException {
        DataInputStream dis = new DataInputStream(m_InputStream);

        // Read the encrypted message
        int messageLength = dis.readInt();
        byte[] encryptedMessage = new byte[messageLength];
        dis.readFully(encryptedMessage);

        // Decrypt into byte array
        CipherTools clientCipher = null;
        try {
            clientCipher = new CipherTools(m_SessionKey, CipherTools.GenerateIV());
            return clientCipher.decrypt(encryptedMessage);
        } catch (Exception e) {
            logError("Failed to read session encrypted message.");
            e.printStackTrace();
            return null;
        }
    }

    protected void writeEncrypted(OutputStream outputStream, byte[] message) throws IOException {
        DataOutputStream dos = new DataOutputStream(outputStream);
        // Encrypt into byte array
        CipherTools clientCipher = null;
        try {
            clientCipher = new CipherTools(m_SessionKey, CipherTools.GenerateIV());
            byte[] encrypted = clientCipher.encrypt(message);
            dos.write(AuthenticationProtocol.HEADER_SESSION_ENCRYPTED);
            dos.writeInt(encrypted.length);
            dos.write(encrypted);
            dos.flush();
        } catch (Exception e) {
            logError("Failed to write session encrypted message.");
            e.printStackTrace();
            return;
        }
    }

    protected abstract String getLogTag();

    protected void log(String message) {
        System.out.println(getLogTag() + message);
    }

    protected void logError(String message) {
        System.out.println(getLogTag() + message);
    }
}
