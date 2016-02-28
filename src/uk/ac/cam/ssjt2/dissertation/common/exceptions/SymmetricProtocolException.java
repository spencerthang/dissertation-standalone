package uk.ac.cam.ssjt2.dissertation.common.exceptions;

/**
 * Created by Spencer on 22/11/2015.
 */
public class SymmetricProtocolException extends Exception {

    private final Exception m_InnerException;

    public SymmetricProtocolException(String message) {
        super(message);
        m_InnerException = null;
    }

    public SymmetricProtocolException(Exception exception) {
        super(exception.getMessage());
        m_InnerException = exception;
    }

}
