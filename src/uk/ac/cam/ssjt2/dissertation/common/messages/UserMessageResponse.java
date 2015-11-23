package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.Message;

import java.io.IOException;

/**
 * Created by Spencer on 23/11/2015.
 */
public class UserMessageResponse extends Message {

    private final String Response;

    public UserMessageResponse(String response) throws IOException {
        super(AuthenticationProtocol.HEADER_SERVER_USER_MESSAGE_RESPONSE);
        Response = response;
    }

    public String getResponse() {
        return Response;
    }

}
