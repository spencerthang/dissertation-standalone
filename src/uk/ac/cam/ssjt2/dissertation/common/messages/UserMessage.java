package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.Message;

import java.io.IOException;

/**
 * Created by Spencer on 23/11/2015.
 */
public class UserMessage extends Message {

    private final String Request;
    private final String RequestType;

    public UserMessage(String request) throws IOException {
        this(request, "GET");
    }

    public UserMessage(String request, String requestType) throws IOException {
        super(AuthenticationProtocol.HEADER_SERVER_USER_MESSAGE);
        Request = request;
        RequestType = requestType;
    }

    public String getRequest() {
        return Request;
    }

    public String getRequestType() {
        return RequestType;
    }

}
