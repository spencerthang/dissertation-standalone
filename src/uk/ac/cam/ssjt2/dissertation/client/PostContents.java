package uk.ac.cam.ssjt2.dissertation.client;

import uk.ac.cam.ssjt2.dissertation.common.Message;

/**
 * Created by Spencer on 23/11/2015.
 */
public class PostContents {

    private String m_Data;

    public PostContents(Message message) {
        m_Data = message.getJson();
    }

    public String getData() {
        return m_Data;
    }

    public void setData(String data) {
        m_Data = data;
    }

    public String getSessionId() {
        return null;
    }

    public String getBase64EncodedIV() {
        return null;
    }

}
