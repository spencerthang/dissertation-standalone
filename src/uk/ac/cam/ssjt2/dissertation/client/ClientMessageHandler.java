package uk.ac.cam.ssjt2.dissertation.client;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.MessageHandlerBase;
import uk.ac.cam.ssjt2.dissertation.common.messages.TestMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Spencer on 3/11/2015.
 */
public class ClientMessageHandler extends MessageHandlerBase {

    private final AuthenticationClient m_Client;

    public ClientMessageHandler(InputStream inputStream, OutputStream outputStream, AuthenticationClient client) {
        super(inputStream, outputStream);
        m_Client = client;
    }

    @Override
    public void handleMessage() throws IOException {
        byte header = (byte) m_InputStream.read();
        System.out.println("[Client] Received packet header: " + header);

        switch(header) {
            case AuthenticationProtocol.HEADER_TEST:
                System.out.println("[Client] Received test message.");
                m_Client.UnhandledMessages.add(new TestMessage().readFromStream(m_InputStream));
                break;
            default:
                throw new IllegalArgumentException("Unrecognized message header: " + header);
        }
    }

}
