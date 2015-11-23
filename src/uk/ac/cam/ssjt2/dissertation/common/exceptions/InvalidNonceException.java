package uk.ac.cam.ssjt2.dissertation.common.exceptions;

/**
 * Created by Spencer on 22/11/2015.
 */
public class InvalidNonceException extends SymmetricProtocolException {

    public InvalidNonceException(String message) {
        super(message);
    }

}
