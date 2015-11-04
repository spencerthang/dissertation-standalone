package uk.ac.cam.ssjt2.dissertation;

import java.io.*;
import java.net.Socket;

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

    public String testConnection() throws IOException {
        try(final Socket s = new Socket(m_ServerAddress, m_ServerPort)) {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
                out.println("test");
                return in.readLine();
            }
        }
    }
}
