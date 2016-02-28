package uk.ac.cam.ssjt2.dissertation.tests;

import org.junit.Test;
import uk.ac.cam.ssjt2.dissertation.common.messages.KDCRequestMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Spencer on 16/11/2015.
 */
public class PacketJsonTests {

    @Test
    public void kdcRequestMessageIsValid() throws IOException, NoSuchAlgorithmException {
        KDCRequestMessage message = new KDCRequestMessage(123, 456, 789);
        String expected = "{\"ClientId\":123,\"TargetId\":456,\"ClientNonce\":789,\"ClientIV\":\"d0xTO0yqNYRzzOcw\",\"Header\":1}";
        assertEquals(expected, message.getJson());
    }

}
