package uk.ac.cam.ssjt2.dissertation.common.messages;

import uk.ac.cam.ssjt2.dissertation.common.AuthenticationProtocol;
import uk.ac.cam.ssjt2.dissertation.common.Message;

import java.io.IOException;

/**
 * Created by Spencer on 23/11/2015.
 */
public class ServerAuthenticationStatusMessage extends Message {

    private final boolean Authenticated;

    public ServerAuthenticationStatusMessage(boolean authenticated) throws IOException {
        super(AuthenticationProtocol.HEADER_SERVER_AUTHENTICATION_STATUS);
        Authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return Authenticated;
    }
}
