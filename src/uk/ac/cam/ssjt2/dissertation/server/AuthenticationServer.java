package uk.ac.cam.ssjt2.dissertation.server;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Spencer on 5/11/2015.
 */
public class AuthenticationServer implements Runnable {

    private final int m_Port;
    private final int m_ServerId;
    private final SecretKey m_ServerKey;

    private Map<Integer, SecretKey> m_KeyStore = new ConcurrentHashMap<>();

    public AuthenticationServer(int port, int serverId, SecretKey serverKey) {
        m_Port = port;
        m_ServerId = serverId;
        m_ServerKey = serverKey;
    }

    public void addKey(int nodeId, SecretKey key) {
        m_KeyStore.put(nodeId, key);
    }

    public SecretKey getKey(int nodeId) {
        return m_KeyStore.get(nodeId);
    }

    @Override
    public void run() {
        try(ServerSocket socket = new ServerSocket(m_Port)) {
            while(true) {
                Socket client = socket.accept();
                ServerMessageHandler handler = new ServerMessageHandler(client.getInputStream(), client.getOutputStream(), this);
                Thread handlerThread = new Thread(handler);
                handlerThread.start();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    protected SecretKey getServerKey() {
        return m_ServerKey;
    }
}
