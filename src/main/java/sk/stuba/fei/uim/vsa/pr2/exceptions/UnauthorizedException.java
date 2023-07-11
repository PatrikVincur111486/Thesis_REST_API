package sk.stuba.fei.uim.vsa.pr2.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class UnauthorizedException extends WebApplicationException {
    public UnauthorizedException() {
        super(Response.Status.UNAUTHORIZED.toString(), Response.Status.UNAUTHORIZED.getStatusCode());
    }
}
