package sk.stuba.fei.uim.vsa.pr2.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr2.dto.Error;
import sk.stuba.fei.uim.vsa.pr2.dto.MessageDto;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<WebApplicationException> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Response toResponse(WebApplicationException e) {
        MessageDto errorMessage = new MessageDto();
        errorMessage.setCode(e.getResponse().getStatus());
        errorMessage.setMessage(e.getMessage());

        Error errorDetail = new Error();
        errorDetail.setType(e.getClass().getName());
        errorDetail.setTrace(Arrays.toString(e.getStackTrace()));

        errorMessage.setError(errorDetail);
        String json;
        try {
            json = objectMapper.writeValueAsString(errorMessage);
        } catch (JsonProcessingException exc) {
            exc.printStackTrace();
            json = "{}";
        }
        if(e.getResponse().getStatus()==401){
            return Response.status(e.getResponse().getStatus()).entity(json).type(MediaType.APPLICATION_JSON).header(HttpHeaders.WWW_AUTHENTICATE, "").build();

        }else {
            return Response.status(e.getResponse().getStatus()).entity(json).type(MediaType.APPLICATION_JSON).build();
        }
    }

}
