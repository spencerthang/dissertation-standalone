package uk.ac.cam.ssjt2.dissertation.client;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.MessageHandlerBase;
import uk.ac.cam.ssjt2.dissertation.common.messages.KDCRequestMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.KDCResponseMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.TestMessage;
import uk.ac.cam.ssjt2.dissertation.kdc.AuthenticationKDC;

import javax.crypto.SecretKey;
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
            case AuthenticationProtocol.HEADER_KDC_REQUEST:
                System.out.println("[KDC] Received KDC request message.");
                KDCRequestMessage message = KDCRequestMessage.readFromStream(m_InputStream);
                System.out.println("[KDC] KDC request from client " + message.getClientId() + " for target " + message.getTargetId() + " with nonce " + message.getClientNonce());

                SecretKey clientKey = m_KDC.getKey(message.getClientId());
                SecretKey targetKey = m_KDC.getKey(message.getTargetId());
                try {
                    SecretKey sessionKey = CipherTools.GenerateSecretKey();
                    KDCResponseMessage response = new KDCResponseMessage(message.getClientId(), clientKey, message.getTargetId(), targetKey, message.getClientNonce(), sessionKey);
                    m_OutputStream.write(response.getBytes());
                } catch (Exception e) {
                    System.err.println("[KDC] Error occurred while forming KDC response to " + message.getClientId());
                    e.printStackTrace();
                }
                break;
            default:
                //throw new IllegalArgumentException("Unrecognized message header: " + header);
        }
    }

}
