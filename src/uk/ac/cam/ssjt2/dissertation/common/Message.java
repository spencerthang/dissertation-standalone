package uk.ac.cam.ssjt2.dissertation.common;

import com.google.gson.Gson;

/**
 * Created by Spencer on 2/11/2015.
 */
public class Message {

    private final byte Header;

    public Message(byte header) {
        Header = header;
    }

    public String getJson() {
        Gson gson  = new Gson();
        return gson.toJson(this);
    }
}
