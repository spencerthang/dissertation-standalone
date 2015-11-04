package uk.ac.cam.ssjt2.dissertation;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;

import java.io.*;
import java.net.Socket;

/**
 * Created by Spencer on 1/11/2015.
 */
public class AuthenticationClient implements AutoCloseable {

    private String m_ServerAddress;
    private int m_ServerPort;
    private Socket m_Client = null;

    public AuthenticationClient(String serverAddress, int serverPort) {
        m_ServerAddress = serverAddress;
        m_ServerPort = serverPort;
    }

    public boolean connect() throws IOException {
        m_Client = new Socket(m_ServerAddress, m_ServerPort);
        return m_Client != null && m_Client.isConnected();
    }

    @Override
    public void close() throws Exception {
        if(m_Client != null) {
            m_Client.close();
        }
    }
}
