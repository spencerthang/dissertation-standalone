package uk.ac.cam.ssjt2.dissertation.client;

import uk.ac.cam.ssjt2.dissertation.common.CipherTools;
import uk.ac.cam.ssjt2.dissertation.common.Message;

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
 * Created by Spencer on 23/11/2015.
 */
public class EncryptedPostContents extends PostContents {

    private final String m_IV;
    private final String m_SessionId;

    public EncryptedPostContents(Message message, SecretKey key, String sessionId) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        super(message);

        IvParameterSpec iv = CipherTools.GenerateIV();
        m_IV = DatatypeConverter.printBase64Binary(iv.getIV());

        CipherTools cipher = new CipherTools(key, iv);
        byte[] encrypted = cipher.encrypt(getData().getBytes());
        setData(DatatypeConverter.printBase64Binary(encrypted));

        m_SessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return m_SessionId;
    }

    @Override
    public String getBase64EncodedIV() {
        return m_IV;
    }

}
