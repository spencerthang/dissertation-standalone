package uk.ac.cam.ssjt2.dissertation.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Spencer on 2/11/2015.
 */
public abstract class MessageBase implements AutoCloseable {

    private final byte m_Header;
    protected final ByteArrayOutputStream m_Buffer = new ByteArrayOutputStream();

    public MessageBase(byte header) {
        m_Header = header;
        m_Buffer.write(header);
    }

    public byte[] getBytes() {
        return m_Buffer.toByteArray();
    }

    public abstract MessageBase readFromStream(InputStream inputStream) throws IOException;

    @Override
    public void close() throws Exception {
        if(m_Buffer != null) {
            m_Buffer.close();
        }
    }
}
