package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr1.ThesisService;
import sk.stuba.fei.uim.vsa.pr1.entities.Pedagog;
import sk.stuba.fei.uim.vsa.pr1.entities.Student;
import sk.stuba.fei.uim.vsa.pr2.auth.Authentication;
import sk.stuba.fei.uim.vsa.pr2.dto.CreateStudentRequestDto;
import sk.stuba.fei.uim.vsa.pr2.dto.StudentDto;
import sk.stuba.fei.uim.vsa.pr2.exceptions.ISEException;
import sk.stuba.fei.uim.vsa.pr2.exceptions.UnauthorizedException;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/students")

public class StudentResource {
    private final ThesisService thesisService = new ThesisService();
    private final ObjectMapper json = new ObjectMapper();
    public Authentication auth = new Authentication();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader){
        try {
            Object user = auth.authenticateUser(authHeader);
            if(user==null){
                throw new UnauthorizedException();
            }

            List<Student> allStudents = thesisService.getStudents();
            List<StudentDto> allStudentsDto = new ArrayList<>();
            for(Student s:allStudents){
                allStudentsDto.add(new StudentDto(s));
            }
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(allStudentsDto)).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Long id,@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader){
        try {

            Object user = auth.authenticateUser(authHeader);
            if(user==null){
                throw new UnauthorizedException();
            }
            Student student = thesisService.getStudent(id);
            if(student==null){
                throw new NotFoundException();
            }
            StudentDto dto = new StudentDto(student);
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
            CreateStudentRequestDto request = json.readValue(body, CreateStudentRequestDto.class);
            Student s=thesisService.createStudent(request.getAisId(),request.getName(),request.getEmail(), request.getPassword());
            if(s==null){
                throw new ISEException();
            }
            s.setSemester(request.getTerm());
            s.setProgram(request.getProgramme());
            s.setRocnik(request.getYear());
            thesisService.updateStudent(s);
            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(new StudentDto(s))).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id,@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader){
        try {

            Object user = auth.authenticateUser(authHeader);
            if(user==null){
                throw new UnauthorizedException();
            }
            if(!(user instanceof Pedagog) && ((Student) user).getAisId()!=id){
                throw new ForbiddenException();
            }
            Student s = thesisService.deleteStudent(id);
            if(s==null){
                throw new NotFoundException();
            }
            StudentDto dto = new StudentDto(s);
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(dto)).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }
}
