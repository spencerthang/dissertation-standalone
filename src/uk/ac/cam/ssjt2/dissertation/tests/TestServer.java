package uk.ac.cam.ssjt2.dissertation.tests;

import uk.ac.cam.ssjt2.dissertation.common.MessageBase;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Spencer on 1/11/2015.
 */
public class TestServer implements Runnable {

    private final int m_Port;
    private final String m_EchoString;
    public ArrayList<MessageBase> Messages = new ArrayList<MessageBase>();

    public TestServer(int port, String echoString) {
        m_Port = port;
        m_EchoString = echoString;
    }

    @Override
    public void run() {
        try(ServerSocket socket = new ServerSocket(m_Port)) {
            while(true) {
                Socket client = socket.accept();
                TestServerHandler handler = new TestServerHandler(client.getInputStream(), client.getOutputStream(), this);
                while(client.isConnected()) {
                    handler.handleMessage();
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
