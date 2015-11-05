package uk.ac.cam.ssjt2.dissertation.client;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.MessageHandlerBase;
import uk.ac.cam.ssjt2.dissertation.common.messages.KDCResponseMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Spencer on 3/11/2015.
 */
public class ClientKDCMessageHandler extends MessageHandlerBase {

    private final AuthenticationClient m_Client;

    public ClientKDCMessageHandler(InputStream inputStream, OutputStream outputStream, AuthenticationClient client) {
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
                break;
            case AuthenticationProtocol.HEADER_KDC_RESPONSE:
                System.out.println("[Client] Received KDC response message.");
                try {
                    KDCResponseMessage.KDCResponse response = KDCResponseMessage.readFromStream(m_InputStream, m_Client.getClientKey());

                    if(response.getClientNonce() != m_Client.getNonce()) {
                        System.err.println("[Client] KDC response verification failed, nonce mismatch. Expected: " + m_Client.getNonce() + ", expected: " + response.getClientNonce());
                    } else if(response.getTargetId() != m_Client.getTargetId()) {
                        System.err.println("[Client] KDC response verification failed, targetId mismatch. Expected: " + m_Client.getTargetId() + ", expected: " + response.getTargetId());
                    } else {
                        System.out.println("[Client] KDC response decoded, target: " + response.getTargetId() + ", nonce: " + response.getClientNonce());
                    }
                } catch (Exception e) {
                    System.err.println("[Client] Error occurred while decoding KDC response");
                    e.printStackTrace();
                }
                break;
            default:
                System.err.println("Unrecognized message header: " + header);
        }
    }

}
