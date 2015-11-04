package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.MessageBase;

import java.io.InputStream;

/**
 * Created by Spencer on 2/11/2015.
 */
public class TestMessage extends MessageBase {

    public TestMessage() {
        super(AuthenticationProtocol.HEADER_TEST);
    }

    public static TestMessage readFromStream(InputStream inputStream) {
        return new TestMessage();
    }
}
