package uk.ac.cam.ssjt2.dissertation.common;

import com.google.gson.Gson;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;
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
        EncryptedResponse encryptedResponse = gson.fromJson(response, EncryptedResponse.class);
        IvParameterSpec iv = new IvParameterSpec(encryptedResponse.getIV());

        // Decrypt KDC Response
        // Decrypt into byte array
        CipherTools clientCipher = null;
        try {
            clientCipher = new CipherTools(key, iv);
            String decryptedJson = new String(clientCipher.decrypt(encryptedResponse.getData()));
            return gson.fromJson(decryptedJson, Message.class);
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

    public class EncryptedResponse {
        String Data;
        String IV;

        public byte[] getData() {
            return DatatypeConverter.parseBase64Binary(Data);
        }

        public byte[] getIV() {
            return DatatypeConverter.parseBase64Binary(IV);
        }
    }
}
