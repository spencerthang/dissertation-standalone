package uk.ac.cam.ssjt2.dissertation.client;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.MessageBase;
import uk.ac.cam.ssjt2.dissertation.common.MessageHandlerBase;
import uk.ac.cam.ssjt2.dissertation.common.messages.TestMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Spencer on 3/11/2015.
 */
public class ClientMessageHandler extends MessageHandlerBase {

    public ClientMessageHandler(InputStream inputStream, OutputStream outputStream) {
        super(inputStream, outputStream);
    }

    @Override
    public MessageBase handleMessage() throws IOException {
        byte header = (byte) m_InputStream.read();

        switch(header) {
            case AuthenticationProtocol.HEADER_TEST:
                return new TestMessage().readFromStream(m_InputStream);
            default:
                throw new IllegalArgumentException("Unrecognized message header: " + header);
        }
    }

}
