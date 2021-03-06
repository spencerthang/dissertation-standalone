package uk.ac.cam.ssjt2.dissertation.common;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import uk.ac.cam.ssjt2.dissertation.common.exceptions.SymmetricProtocolException;
import uk.ac.cam.ssjt2.dissertation.common.messages.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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

    public static Message fromEncryptedResponse(SecretKey key, String response) throws SymmetricProtocolException {
        // Extract IV and Encrypted Message
        Gson gson = new Gson();
        EncryptedMessage encryptedResponse;
        try {
            encryptedResponse = gson.fromJson(response, EncryptedMessage.class);
        } catch (JsonSyntaxException e) {
            throw new SymmetricProtocolException("Response is not valid JSON: " + response);
        }

        // Check if an error occured
        if(encryptedResponse.getError() != null) {
            throw new SymmetricProtocolException(encryptedResponse.getError());
        }

        // Decrypt KDC Response
        // Decrypt into byte array
        try {
            IvParameterSpec iv = new IvParameterSpec(encryptedResponse.getIV());
            CipherTools clientCipher = new CipherTools(key, iv);

            // Verify MAC
            byte[] actual = clientCipher.mac(encryptedResponse.getData());
            byte[] expected = encryptedResponse.getHMAC();
            if(!Arrays.equals(expected, actual)) {
                throw new SymmetricProtocolException("HMAC of message failed to verify");
            }

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
                case AuthenticationProtocol.HEADER_SERVER_USER_MESSAGE_RESPONSE:
                    return gson.fromJson(decryptedJson, UserMessageResponse.class);
                default:
                    return header;
            }

        } catch (GeneralSecurityException e) {
            throw new SymmetricProtocolException(e);
        }
    }

    public byte getHeader() {
        return Header;
    }
}
