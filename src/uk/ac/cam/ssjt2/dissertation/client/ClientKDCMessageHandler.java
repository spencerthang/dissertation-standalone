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
    public void processMessage(InputStream inputStream, byte header) throws IOException {
        log("Received packet header: " + header);

        switch(header) {
            case AuthenticationProtocol.HEADER_TEST:
                log("Received test message.");
                break;
            case AuthenticationProtocol.HEADER_KDC_RESPONSE:
                log("Received KDC response message.");
                try {
                    KDCResponseMessage.KDCResponse response = KDCResponseMessage.readFromStream(inputStream, m_Client.getClientKey());

                    if(response.getClientNonce() != m_Client.getNonce()) {
                        logError("KDC response verification failed, nonce mismatch. Expected: " + m_Client.getNonce() + ", expected: " + response.getClientNonce());
                    } else if(response.getTargetId() != m_Client.getTargetId()) {
                        logError("KDC response verification failed, targetId mismatch. Expected: " + m_Client.getTargetId() + ", expected: " + response.getTargetId());
                    } else {
                        log("KDC response decoded, target: " + response.getTargetId() + ", nonce: " + response.getClientNonce());
                    }

                    m_Client.setSessionKey(response.getSessionKey());
                    m_Client.setEncryptedMessageToServer(response.getTargetEncryptedMessage());
                } catch (Exception e) {
                    logError("Error occurred while decoding KDC response");
                    e.printStackTrace();
                }
                break;
            default:
                logError("Unrecognized message header: " + header);
        }
    }

    @Override
    protected String getLogTag() {
        return "[Client " + m_Client.getClientId() + "] ";
    }
}
