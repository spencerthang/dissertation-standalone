package uk.ac.cam.ssjt2.dissertation.client;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.MessageHandlerBase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Spencer on 5/11/2015.
 */
public class ClientServerMessageHandler extends MessageHandlerBase {

    private final AuthenticationClient m_Client;

    public ClientServerMessageHandler(InputStream inputStream, OutputStream outputStream, AuthenticationClient client) {
        super(inputStream, outputStream);
        m_Client = client;
    }

    @Override
    public void handleMessage() throws IOException {
        byte header = (byte) m_InputStream.read();
        log("Received packet header: " + header);

        switch(header) {
            case AuthenticationProtocol.HEADER_TEST:
                log("Received test message.");
                break;
            default:
                logError("Unrecognized message header: " + header);
        }
    }

    public void log(String message) {
        System.out.println("[Client " + m_Client.getClientId() + "] " + message);
    }

    public void logError(String message) {
        System.out.println("[Client " + m_Client.getClientId() + "] " + message);
    }
}