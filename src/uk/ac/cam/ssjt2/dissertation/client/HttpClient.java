package uk.ac.cam.ssjt2.dissertation.client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Spencer on 16/11/2015.
 */
public class HttpClient {

    private final URL m_Url;

    public HttpClient(String url) throws IOException {
        m_Url = new URL(url);
    }

    // Post unencrypted data (no IV)
    public String post(String data) throws IOException {
        return post(data, null);
    }

    // Post encrypted data (with IV) or unencrypted data (IV = null)
    public String post(String data, String iv) throws IOException {
        String urlParameters = "Data=" + data;
        if(iv != null) urlParameters += "&IV=" + iv;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        HttpURLConnection httpConnection = (HttpURLConnection) m_Url.openConnection();
        httpConnection.setDoOutput(true);
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConnection.setRequestProperty("charset", "utf8");
        httpConnection.setUseCaches(false);
        httpConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));

        StringBuffer response = new StringBuffer();
        try(DataOutputStream dataOutputStream = new DataOutputStream(httpConnection.getOutputStream())) {
            // Send POST data
            dataOutputStream.write(postData);
            dataOutputStream.flush();
        }

        try(BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()))) {
            String inputLine;
            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        return response.toString();
    }

}
