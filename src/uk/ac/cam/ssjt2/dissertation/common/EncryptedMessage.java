package uk.ac.cam.ssjt2.dissertation.common;

import javax.xml.bind.DatatypeConverter;

/**
 * Created by Spencer on 22/11/2015.
 */
public class EncryptedMessage {
    String Data;
    String IV;
    String Error;
    String HMAC;

    public EncryptedMessage(String data, String iv, String error, String hmac) {
        Data = data;
        IV = iv;
        Error = error;
        HMAC = hmac;
    }

    public byte[] getData() {
        return DatatypeConverter.parseBase64Binary(Data);
    }

    public byte[] getIV() {
        return DatatypeConverter.parseBase64Binary(IV);
    }

    public String getError() { return Error; }

    public byte[] getHMAC() {
        return DatatypeConverter.parseBase64Binary(HMAC);
    }
}
