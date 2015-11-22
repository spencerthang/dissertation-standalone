package uk.ac.cam.ssjt2.dissertation.common;

import javax.xml.bind.DatatypeConverter;

/**
 * Created by Spencer on 22/11/2015.
 */
public class EncryptedMessage {
    String Data;
    String IV;

    public EncryptedMessage(String data, String iv) {
        Data = data;
        IV = iv;
    }

    public byte[] getData() {
        return DatatypeConverter.parseBase64Binary(Data);
    }

    public byte[] getIV() {
        return DatatypeConverter.parseBase64Binary(IV);
    }
}
