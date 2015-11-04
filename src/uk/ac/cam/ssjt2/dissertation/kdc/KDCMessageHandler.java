package uk.ac.cam.ssjt2.dissertation.client;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.MessageHandlerBase;
import uk.ac.cam.ssjt2.dissertation.common.messages.TestMessage;
import uk.ac.cam.ssjt2.dissertation.kdc.AuthenticationKDC;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Spencer on 4/11/2015.
 */
public class KDCMessageHandler extends MessageHandlerBase {

    private final AuthenticationKDC m_KDC;

    public KDCMessageHandler(InputStream inputStream, OutputStream outputStream, AuthenticationKDC kdc) {
        super(inputStream, outputStream);
        m_KDC = kdc;
    }

    @Override
    public void handleMessage() throws IOException {
        byte header = (byte) m_InputStream.read();
        System.out.println("[KDC] Received packet header: " + header);

        switch(header) {
            case AuthenticationProtocol.HEADER_TEST:
                System.out.println("[KDC] Received test message.");
                m_KDC.UnhandledMessages.add(new TestMessage().readFromStream(m_InputStream));
                break;
            default:
                throw new IllegalArgumentException("Unrecognized message header: " + header);
        }
    }

}
