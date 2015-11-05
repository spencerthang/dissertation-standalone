package uk.ac.cam.ssjt2.dissertation.common;

import java.io.*;

/**
 * Created by Spencer on 3/11/2015.
 */
public abstract class MessageHandlerBase implements Runnable, AutoCloseable {

    protected final InputStream m_InputStream;
    protected final OutputStream m_OutputStream;
    protected final boolean m_LeaveOpen;

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
                handleMessage();
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

    public abstract void handleMessage() throws IOException;
}
