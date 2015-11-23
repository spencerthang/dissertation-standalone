package uk.ac.cam.ssjt2.dissertation.common;

import com.google.gson.Gson;
import uk.ac.cam.ssjt2.dissertation.common.messages.KDCResponseMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.ServerAuthenticationStatusMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.ServerChallengeMessage;
import uk.ac.cam.ssjt2.dissertation.common.messages.ServerChallengeResponseMessage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Spencer on 2/11/2015.
 */
public class Message {

    private final byte Header;

    public Message(byte header) {
        Header = header;
    }

    public String getJson() {
        Gson gson  = new Gson();
        return gson.toJson(this);
    }

    public static Message fromEncryptedResponse(SecretKey key, String response) {
        // Extract IV and Encrypted Message
        Gson gson = new Gson();
        EncryptedMessage encryptedResponse = gson.fromJson(response, EncryptedMessage.class);
        IvParameterSpec iv = new IvParameterSpec(encryptedResponse.getIV());

        // Decrypt KDC Response
        // Decrypt into byte array
        try {
            CipherTools clientCipher = new CipherTools(key, iv);
            String decryptedJson = new String(clientCipher.decrypt(encryptedResponse.getData()));

            // Decode to message to determine the packet header
            Message header  = gson.fromJson(decryptedJson, Message.class);

            switch(header.getHeader()) {
                case AuthenticationProtocol.HEADER_KDC_RESPONSE:
                    return gson.fromJson(decryptedJson, KDCResponseMessage.class);
                case AuthenticationProtocol.HEADER_SERVER_CHALLENGE:
                    return gson.fromJson(decryptedJson, ServerChallengeMessage.class);
                case AuthenticationProtocol.HEADER_SERVER_AUTHENTICATION_STATUS:
                    return gson.fromJson(decryptedJson, ServerAuthenticationStatusMessage.class);
                default:
                    return header;
            }

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte getHeader() {
        return Header;
    }
}
