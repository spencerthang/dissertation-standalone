package uk.ac.cam.ssjt2.dissertation.tests;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.MessageBase;
import uk.ac.cam.ssjt2.dissertation.common.MessageHandlerBase;
import uk.ac.cam.ssjt2.dissertation.common.messages.TestMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Spencer on 2/11/2015.
 */
public class TestServerHandler extends MessageHandlerBase {

    private final TestServer m_TestServer;

    public TestServerHandler(InputStream inputStream, OutputStream outputStream, TestServer testServer) {
        super(inputStream, outputStream);
        m_TestServer = testServer;
    }

    @Override
    public void handleMessage() throws IOException {
        byte header = (byte) m_InputStream.read();
        System.out.println("[TestServer] Received packet header: " + header);

        switch(header) {
            case AuthenticationProtocol.HEADER_TEST:
                m_OutputStream.write(new TestMessage().getBytes());
                m_TestServer.Messages.add(TestMessage.readFromStream(m_InputStream));
                break;
            default:
                throw new IllegalArgumentException("Unrecognized message header: " + header);
        }
    }
}
