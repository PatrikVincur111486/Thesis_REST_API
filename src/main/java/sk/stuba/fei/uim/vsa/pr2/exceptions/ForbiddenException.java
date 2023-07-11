package sk.stuba.fei.uim.vsa.pr2.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ForbiddenException extends WebApplicationException {
    public ForbiddenException() {
        super(Response.Status.FORBIDDEN.toString(), Response.Status.FORBIDDEN.getStatusCode());
    }
}
