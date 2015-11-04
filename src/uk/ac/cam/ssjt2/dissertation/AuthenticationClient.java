package uk.ac.cam.ssjt2.dissertation;

/**
 * Created by Spencer on 1/11/2015.
 */
public class AuthenticationClient {

    private String m_ServerAddress;
    private int m_ServerPort;

    public AuthenticationClient(String c_ServerAddress, int c_ServerPort) {
        m_ServerAddress = c_ServerAddress;
        m_ServerPort = c_ServerPort;
    }

}
