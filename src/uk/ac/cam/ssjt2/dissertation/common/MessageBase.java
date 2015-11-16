package uk.ac.cam.ssjt2.dissertation.common;

import com.google.gson.Gson;

/**
 * Created by Spencer on 2/11/2015.
 */
public abstract class MessageBase {

    private final byte Header;

    public MessageBase(byte header) {
        Header = header;
    }

    public String getJson() {
        Gson gson  = new Gson();
        return gson.toJson(this);
    }
}
