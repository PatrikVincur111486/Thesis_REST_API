package sk.stuba.fei.uim.vsa.pr2.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ISEException extends WebApplicationException {
    public ISEException() {
        super(Response.Status.INTERNAL_SERVER_ERROR.toString(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
}
