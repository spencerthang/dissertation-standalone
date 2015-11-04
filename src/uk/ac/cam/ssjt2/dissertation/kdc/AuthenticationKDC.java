package uk.ac.cam.ssjt2.dissertation.kdc;

import uk.ac.cam.ssjt2.dissertation.client.KDCMessageHandler;
import uk.ac.cam.ssjt2.dissertation.common.MessageBase;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Spencer on 4/11/2015.
 */
public class AuthenticationKDC implements Runnable {

    private final int m_Port;
    private Thread m_ServerThread = null;

    private Map<Integer, SecretKey> m_KeyStore = new ConcurrentHashMap<>();
    public ArrayList<MessageBase> UnhandledMessages = new ArrayList<MessageBase>();

    public AuthenticationKDC(int port) {
        m_Port = port;
    }

    public void addKey(int nodeId, SecretKey key) {
        m_KeyStore.put(nodeId, key);
    }

    public void sign(int nodeId, byte[] bytes) {

    }

    @Override
    public void run() {
        try(ServerSocket socket = new ServerSocket(m_Port)) {
            while(true) {
                Socket client = socket.accept();
                KDCMessageHandler handler = new KDCMessageHandler(client.getInputStream(), client.getOutputStream(), this);
                Thread handlerThread = new Thread(handler);
                handlerThread.start();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }


}
