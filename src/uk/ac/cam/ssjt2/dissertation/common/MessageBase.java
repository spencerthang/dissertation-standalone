package uk.ac.cam.ssjt2.dissertation.common;

import java.io.*;

/**
 * Created by Spencer on 2/11/2015.
 */
public abstract class MessageBase implements AutoCloseable {

    private final byte m_Header;
    private final ByteArrayOutputStream m_ByteBuffer = new ByteArrayOutputStream();
    protected final DataOutputStream m_Buffer = new DataOutputStream(m_ByteBuffer);

    public MessageBase(byte header) {
        m_Header = header;
        m_ByteBuffer.write(header);
    }

    public byte[] getBytes() throws IOException {
        m_Buffer.flush();
        return m_ByteBuffer.toByteArray();
    }

    @Override
    public void close() throws Exception {
        if(m_Buffer != null) {
            m_Buffer.close();
        }
    }
}
