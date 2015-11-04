package uk.ac.cam.ssjt2.dissertation;

/**
 * Created by Spencer on 1/11/2015.
 */
public class AuthenticationClient {

    private String m_ServerAddress;
    private int m_ServerPort;

    public AuthenticationClient(String serverAddress, int serverPort) {
        m_ServerAddress = serverAddress;
        m_ServerPort = serverPort;
    }

}
