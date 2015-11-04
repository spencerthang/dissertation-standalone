package uk.ac.cam.ssjt2.dissertation.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Spencer on 1/11/2015.
 */
public class TestServer implements Runnable {

    private final int m_Port;
    private final String m_EchoString;

    public TestServer(int port, String echoString) {
        m_Port = port;
        m_EchoString = echoString;
    }

    @Override
    public void run() {
        try(ServerSocket socket = new ServerSocket(m_Port)) {
            while(true) {
                Socket client = socket.accept();
                try(BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        out.println(m_EchoString != null ? m_EchoString : inputLine);
                        if(inputLine.equals("exit"))
                            break;
                    }
                }
                client.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
