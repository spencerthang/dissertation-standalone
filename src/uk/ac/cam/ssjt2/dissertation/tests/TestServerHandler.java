package uk.ac.cam.ssjt2.dissertation.tests;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.MessageBase;
import uk.ac.cam.ssjt2.dissertation.common.messages.TestMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Spencer on 2/11/2015.
 */
public class TestServerHandler {

    public MessageBase handleMessage(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte header = (byte) inputStream.read();

        switch(header) {
            case AuthenticationProtocol.HEADER_TEST:
                outputStream.write(new TestMessage().getBytes());
                return new TestMessage().readFromStream(inputStream);
            default:
                throw new IllegalArgumentException("Unrecognized message header: " + header);
        }
    }

}
