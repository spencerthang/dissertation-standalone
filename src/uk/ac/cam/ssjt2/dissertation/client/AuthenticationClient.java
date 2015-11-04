package uk.ac.cam.ssjt2.dissertation.client;

import uk.ac.cam.ssjt2.dissertation.common.MessageBase;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Spencer on 1/11/2015.
 */
public class AuthenticationClient implements AutoCloseable {

    private String m_ServerAddress;
    private int m_ServerPort;
    private Socket m_Client = null;
    private ClientMessageHandler m_ClientHandler = null;
    private Thread m_ClientHandlerThread = null;
    public static ArrayList<MessageBase> UnhandledMessages = new ArrayList<MessageBase>();

    public AuthenticationClient(String serverAddress, int serverPort) {
        m_ServerAddress = serverAddress;
        m_ServerPort = serverPort;
    }

    public boolean connect() throws IOException {
        m_Client = new Socket(m_ServerAddress, m_ServerPort);
        if(m_Client != null && m_Client.isConnected()) {
            m_ClientHandler = new ClientMessageHandler(m_Client.getInputStream(), m_Client.getOutputStream(), this);
            m_ClientHandlerThread = new Thread(m_ClientHandler);
            m_ClientHandlerThread.start();
            return true;
        } else {
            return false;
        }
    }

    public void sendMessage(MessageBase message) throws IOException {
        m_Client.getOutputStream().write(message.getBytes());
    }

    @Override
    public void close() throws Exception {
        if(m_Client != null) {
            m_Client.close();
        }
    }
}
