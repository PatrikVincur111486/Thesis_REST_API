package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr1.ThesisService;
import sk.stuba.fei.uim.vsa.pr1.entities.Pedagog;
import sk.stuba.fei.uim.vsa.pr1.entities.Student;
import sk.stuba.fei.uim.vsa.pr2.auth.Authentication;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.dto.CreateTeacherRequestDto;
import sk.stuba.fei.uim.vsa.pr2.dto.TeacherDto;
import sk.stuba.fei.uim.vsa.pr2.exceptions.ISEException;
import sk.stuba.fei.uim.vsa.pr2.exceptions.UnauthorizedException;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/teachers")
public class PedagogResource {
    private final ThesisService thesisService = new ThesisService();
    private final ObjectMapper json = new ObjectMapper();
    public Authentication auth = new Authentication();

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader){
        try {
            Object user = auth.authenticateUser(authHeader);
            if(user==null){
                throw new UnauthorizedException();
            }

            List<Pedagog> allTeachers = thesisService.getTeachers();
            List<TeacherDto> allTeachersDto = new ArrayList<>();
            for(Pedagog t:allTeachers){
                allTeachersDto.add(new TeacherDto(t));
            }
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(allTeachersDto)).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }

    @GET
    @Secured
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Long id,@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader){
        try {
            Object user = auth.authenticateUser(authHeader);
            if(user==null){
                throw new UnauthorizedException();
            }
            Pedagog teacher = thesisService.getTeacher(id);
            if(teacher==null){
                throw new NotFoundException();
            }
            TeacherDto dto = new TeacherDto(teacher);
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(dto)).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body){
        try {
            CreateTeacherRequestDto request = json.readValue(body, CreateTeacherRequestDto.class);
            Pedagog teacher=thesisService.createTeacher(request.getAisId(),request.getName(),request.getEmail(),request.getDepartment() , request.getPassword());
            if(teacher==null){
                throw new ISEException();
            }
            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(new TeacherDto(teacher))).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }

    @DELETE
    @Secured
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id,@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader){
        try {
            Object user = auth.authenticateUser(authHeader);
            if(user==null){
                throw new UnauthorizedException();
            }
            if((user instanceof Student) || ((Pedagog) user).getAisId()!=id){
                throw new ForbiddenException();
            }
            Pedagog t = thesisService.deleteTeacher(id);
            if(t == null){
                throw new NotFoundException();
            }
            TeacherDto dto = new TeacherDto(t);
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(dto)).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }
}
