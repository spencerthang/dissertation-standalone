package uk.ac.cam.ssjt2.dissertation.kdc;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.HexTools;
import uk.ac.cam.ssjt2.dissertation.common.MessageHandlerBase;
import uk.ac.cam.ssjt2.dissertation.common.messages.KDCRequestMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.KDCResponseMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.TestMessage;

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
    public void processMessage(InputStream inputStream, byte header) throws IOException {
        log("Received packet header: " + header);

        switch(header) {
            case AuthenticationProtocol.HEADER_TEST:
                log("Received test message.");
                break;
            case AuthenticationProtocol.HEADER_KDC_REQUEST:
                log("Received KDC request message.");
                KDCRequestMessage message = KDCRequestMessage.readFromStream(inputStream);
                log("KDC request from client " + message.getClientId() + " for target " + message.getTargetId() + " with nonce " + message.getClientNonce());

                SecretKey clientKey = m_KDC.getKey(message.getClientId());
                SecretKey targetKey = m_KDC.getKey(message.getTargetId());
                try {
                    SecretKey sessionKey = CipherTools.GenerateSecretKey();
                    KDCResponseMessage response = new KDCResponseMessage(message.getClientId(), clientKey, message.getTargetId(), targetKey, message.getClientNonce(), sessionKey);
                    m_OutputStream.write(response.getBytes());
                    log("Send KDC response message, generated session key: " + HexTools.bytesToHex(sessionKey.getEncoded()));
                } catch (Exception e) {
                    logError("Error occurred while forming KDC response to " + message.getClientId());
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalArgumentException("Unrecognized message header: " + header);
        }
    }

    public void log(String message) {
        System.out.println("[KDC] " + message);
    }

    public void logError(String message) {
        System.err.println("[KDC] " + message);
    }

}
