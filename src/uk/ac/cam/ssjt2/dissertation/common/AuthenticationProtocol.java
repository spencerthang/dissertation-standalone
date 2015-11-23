package uk.ac.cam.ssjt2.dissertation.common;

/**
 * Created by Spencer on 2/11/2015.
 */
public class AuthenticationProtocol {

    public static final byte HEADER_KDC_REQUEST = 1;
    public static final byte HEADER_KDC_RESPONSE = 2;
    public static final byte HEADER_SERVER_HANDSHAKE = 3;
    public static final byte HEADER_SERVER_CHALLENGE = 4;
    public static final byte HEADER_SERVER_CHALLENGE_RESPONSE = 5;
    public static final byte HEADER_SERVER_AUTHENTICATED_RESPONSE = 6;

}
