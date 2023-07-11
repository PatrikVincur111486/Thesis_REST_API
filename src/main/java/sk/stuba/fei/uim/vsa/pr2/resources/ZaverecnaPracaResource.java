package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr1.ThesisService;
import sk.stuba.fei.uim.vsa.pr1.entities.Pedagog;
import sk.stuba.fei.uim.vsa.pr1.entities.Student;
import sk.stuba.fei.uim.vsa.pr1.entities.ZaverecnaPraca;
import sk.stuba.fei.uim.vsa.pr2.auth.Authentication;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.dto.*;
import sk.stuba.fei.uim.vsa.pr2.exceptions.ISEException;
import sk.stuba.fei.uim.vsa.pr2.exceptions.UnauthorizedException;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("")
public class ZaverecnaPracaResource {
    private final ThesisService thesisService = new ThesisService();
    private final ObjectMapper json = new ObjectMapper();
    public Authentication auth = new Authentication();

    @Path("/theses")
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
            List<ZaverecnaPraca> allTheses = thesisService.getTheses();
            List<ThesisDto> allThesesDto = new ArrayList<>();
            for(ZaverecnaPraca zp:allTheses){
                allThesesDto.add(new ThesisDto(zp));
            }
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(allThesesDto)).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }

    @GET
    @Secured
    @Path("/theses/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Long id,@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader){
        try {
            Object user = auth.authenticateUser(authHeader);
            if(user==null){
                throw new UnauthorizedException();
            }
            ZaverecnaPraca zp = thesisService.getThesis(id);
            if(zp==null){
                throw new NotFoundException();
            }
            ThesisDto dto = new ThesisDto(zp);
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(dto)).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }

    @Path("/theses")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body,@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader){
        try {
            Object user = auth.authenticateUser(authHeader);
            Long supervisorId;
            if(user==null){
                throw new UnauthorizedException();
            }
            if(!(user instanceof Pedagog)){
                throw new ForbiddenException();
            }else {
                supervisorId=((Pedagog) user).getAisId();
            }
            CreateThesisRequestDto request = json.readValue(body, CreateThesisRequestDto.class);
            ZaverecnaPraca zp=thesisService.makeThesisAssignment(supervisorId,request.getTitle(), String.valueOf(request.getType()),request.getDescription(), request.getRegistrationNumber());
            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(new ThesisDto(zp))).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }

    @DELETE
    @Secured
    @Path("/theses/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id,@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader){
        try {
            Object user = auth.authenticateUser(authHeader);
            if(user==null){
                throw new UnauthorizedException();
            }
            if((user instanceof Pedagog)){
                if(thesisService.getThesis(id)!=null && thesisService.getThesis(id).getVeduciPrace().equals(((Pedagog) user).getAisId())){
                    throw new ForbiddenException();
                }
            }else {
                throw new ForbiddenException();
            }
            ZaverecnaPraca zp = thesisService.deleteThesis(id);
            if(zp == null){
                throw new NotFoundException();
            }
            ThesisDto dto = new ThesisDto(zp);
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(dto)).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }

    @POST
    @Path("/theses/{id}/assign")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response assign(String body,@PathParam("id") Long id,@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader){
        try {
            Object user = auth.authenticateUser(authHeader);
            if(user==null){
                throw new UnauthorizedException();
            }
            if(thesisService.getThesis(id)==null){
                throw new NotFoundException();
            }
            if(user instanceof Student){
                ThesisDto zp = new ThesisDto(thesisService.assignThesis(id, ((Student) user).getAisId()));
                return Response.status(Response.Status.OK).entity(zp).build(); //
            }else {
                Long studentId=json.readTree(body).get("studentId").asLong();
                if(thesisService.getStudent(studentId)==null){
                    throw new NotFoundException();
                }
                ThesisDto zp = new ThesisDto(thesisService.assignThesis(id, studentId));
                return Response.status(Response.Status.OK).entity(zp).build(); //
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }

    @POST
    @Path("/theses/{id}/submit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submit(String body,@PathParam("id") Long id,@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            Object user = auth.authenticateUser(authHeader);
            if (user == null) {
                throw new UnauthorizedException();
            }
            if (thesisService.getThesis(id) == null) {
                throw new NotFoundException();
            }
            if (user instanceof Student) {
                if (((Student) user).getZaverecnaPraca().getId() != id) {
                    throw new ForbiddenException();
                }
                thesisService.submitThesis(id);
                ThesisDto zp = new ThesisDto(thesisService.getThesis(id));
                return Response.status(Response.Status.OK).entity(json.writeValueAsString(zp)).build();
            } else {
                Long studentId = json.readTree(body).get("studentId").asLong();
                if (thesisService.getStudent(studentId) == null) {
                    throw new NotFoundException();
                }
                ThesisDto zp = new ThesisDto(thesisService.getThesis(id));
                if (studentId != zp.getAuthor().getId()) {
                    throw new ISEException();
                }
                thesisService.submitThesis(id);
                zp=new ThesisDto(thesisService.getThesis(id));
                return Response.status(Response.Status.OK).entity(json.writeValueAsString(zp)).build();
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e){
            throw new ISEException();
        }
    }
        @POST
        @Path("/search/theses")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        public Response search(String body,@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader){
            try {
                Object user = auth.authenticateUser(authHeader);
                if(user==null){
                    throw new UnauthorizedException();
                }
                JsonNode jsonNode=json.readTree(body);
                if(jsonNode.has("teacherId")){
                    Long teacherId=jsonNode.get("teacherId").asLong();
                    List<ThesisDto> listDto = new ArrayList<>();
                    List<ZaverecnaPraca> list = thesisService.getThesesByTeacher(teacherId);
                    for(ZaverecnaPraca zp:list){
                        listDto.add(new ThesisDto(zp));
                    }
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(listDto)).build();
                } else if (jsonNode.has("studentId")) {
                    Long studentId=jsonNode.get("studentId").asLong();
                    ThesisDto zp=new ThesisDto(thesisService.getThesisByStudent(studentId));
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(zp)).build();
                } else {
                    throw new ISEException();
                }
            } catch (WebApplicationException e) {
                throw e;
            } catch (Exception e){
                throw new ISEException();
            }
    }

}
