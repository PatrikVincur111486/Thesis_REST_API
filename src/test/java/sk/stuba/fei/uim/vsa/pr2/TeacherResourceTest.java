package sk.stuba.fei.uim.vsa.pr2;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr2.model.dto.request.CreateTeacherRequest;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.MessageResponse;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.student.StudentWithThesisResponse;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.teacher.TeacherWithThesesResponse;
import sk.stuba.fei.uim.vsa.pr2.utils.ResourceTest;
import sk.stuba.fei.uim.vsa.pr2.utils.TestData;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr2.StudentResourceTest.createStudent;
import static sk.stuba.fei.uim.vsa.pr2.utils.TestData.ARRAY_CONTENT_LENGTH;
import static sk.stuba.fei.uim.vsa.pr2.utils.TestData.OBJECT_CONTENT_LENGTH;

@Slf4j
public class TeacherResourceTest extends ResourceTest {

    public static final String TEACHER_PATH = "teachers";

    @Test
    public void shouldCreateTeacher() {
        try (Response response = createTeacher(TestData.T01)) {
            response.bufferEntity();
            log.info("Received response " + response);
            TeacherWithThesesResponse body = response.readEntity(TeacherWithThesesResponse.class);
            assertNotNull(body);
            assertEquals(TestData.T01.aisId, body.getAisId());
            assertEquals(TestData.T01.email, body.getEmail());
            assertEquals(TestData.T01.name, body.getName());
            assertTrue(body.getTheses() == null || body.getTheses().isEmpty());
            assertFalse(response.readEntity(String.class).contains("password"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldCallWithoutBodyAndReturnError() {
        try (Response response = request(TEACHER_PATH)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(CreateTeacherRequest.builder().build(),
                        MediaType.APPLICATION_JSON))) {
            testErrorMessage(response, Response.Status.INTERNAL_SERVER_ERROR, Response.Status.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldCreateTeacherWithOnlyRequiredProperties() {
        try (Response response = createResource(TEACHER_PATH, () ->
                        CreateTeacherRequest.builder()
                                .aisId(TestData.T01.aisId)
                                .name(TestData.T01.name)
                                .email(TestData.T01.email)
                                .password(Base64.getEncoder().encodeToString(TestData.T01.password.getBytes()))
                                .build(),
                null)) {
            response.bufferEntity();
            log.info("Received response " + response);
            TeacherWithThesesResponse body = response.readEntity(TeacherWithThesesResponse.class);
            assertNotNull(body);
            assertEquals(TestData.T01.aisId, body.getAisId());
            assertEquals(TestData.T01.email, body.getEmail());
            assertEquals(TestData.T01.name, body.getName());
            assertTrue(body.getTheses() == null || body.getTheses().isEmpty());
            assertFalse(response.readEntity(String.class).contains("password"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldGetAllTeachers() {
        try {
            Long t01Id = Objects.requireNonNull(createTeacher(TestData.T01)).readEntity(TeacherWithThesesResponse.class).getId();
            Long t02Id = Objects.requireNonNull(createTeacher(TestData.T02)).readEntity(TeacherWithThesesResponse.class).getId();

            Response response = request(TEACHER_PATH, TestData.T01).get();
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > ARRAY_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            List<TeacherWithThesesResponse> body = response.readEntity(new GenericType<List<TeacherWithThesesResponse>>() {
            });
            assertNotNull(body);
            assertEquals(2, body.size());
            assertTrue(body.stream().anyMatch(t -> Objects.equals(t.getId(), t01Id)));
            assertTrue(body.stream().anyMatch(t -> Objects.equals(t.getId(), t02Id)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldReturnUnauthorized() {
        try (Response response = request(TEACHER_PATH).get()) {
            testErrorMessage(response, Response.Status.UNAUTHORIZED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldGetEmptyList() {
        try {
            Long s01Id = Objects.requireNonNull(createStudent(TestData.S01)).readEntity(StudentWithThesisResponse.class).getId();
            Response response = request(TEACHER_PATH, TestData.S01).get();
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            List<TeacherWithThesesResponse> body = response.readEntity(new GenericType<List<TeacherWithThesesResponse>>() {
            });
            assertNotNull(body);
            assertTrue(body.isEmpty());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldGetTeacher() {
        Long t01Id = Objects.requireNonNull(createTeacher(TestData.T01)).readEntity(TeacherWithThesesResponse.class).getId();
        try (Response response = request(TEACHER_PATH + "/" + t01Id, TestData.T01).get()) {
            response.bufferEntity();
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            TeacherWithThesesResponse body = response.readEntity(TeacherWithThesesResponse.class);
            assertNotNull(body);
            assertEquals(TestData.T01.aisId, body.getAisId());
            assertEquals(TestData.T01.email, body.getEmail());
            assertEquals(TestData.T01.name, body.getName());
            assertTrue(body.getTheses() == null || body.getTheses().isEmpty());
            assertFalse(response.readEntity(String.class).contains("password"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldGetNotFoundForNonExistingTeacher() {
        Long t01Id = Objects.requireNonNull(createTeacher(TestData.T01)).readEntity(TeacherWithThesesResponse.class).getId();
        try (Response response = request(TEACHER_PATH + "/99", TestData.T01).get()) {
            testErrorMessage(response, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void deleteTeacher() {
        try {
            Long t01Id = Objects.requireNonNull(createTeacher(TestData.T01)).readEntity(TeacherWithThesesResponse.class).getId();

            String path = TEACHER_PATH + "/" + t01Id;
            Response response = request(path, TestData.T01).delete();
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            TeacherWithThesesResponse body = response.readEntity(TeacherWithThesesResponse.class);
            assertNotNull(body);
            assertEquals(TestData.T01.aisId, body.getAisId());
            assertEquals(TestData.T01.email, body.getEmail());
            assertEquals(TestData.T01.name, body.getName());
            assertTrue(body.getTheses() == null || body.getTheses().isEmpty());

            Response nonExisting = request(path, TestData.T01).get();
            assertNotNull(nonExisting);
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), nonExisting.getStatus());
            nonExisting.close();

            Long t02Id = Objects.requireNonNull(createTeacher(TestData.T02)).readEntity(TeacherWithThesesResponse.class).getId();
            Response deleteCheckResponse = request(path, TestData.T02).get();
            assertNotNull(deleteCheckResponse);
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), deleteCheckResponse.getStatus());
            assertTrue(deleteCheckResponse.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(deleteCheckResponse.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, deleteCheckResponse.getMediaType());
            MessageResponse errorMsg = deleteCheckResponse.readEntity(MessageResponse.class);
            assertNotNull(errorMsg);
            assertEquals(404, errorMsg.getCode());
            assertNotNull(errorMsg.getMessage());
            assertFalse(errorMsg.getMessage().isEmpty());
            assertNotNull(errorMsg.getError());
            assertNotNull(errorMsg.getError().getType());
            assertNotNull(errorMsg.getError().getTrace());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldReturnForbiddenToDeleteWithWrongTeacher() {
        try {
            Long t01Id = Objects.requireNonNull(createTeacher(TestData.T01)).readEntity(TeacherWithThesesResponse.class).getId();
            Long t02Id = Objects.requireNonNull(createTeacher(TestData.T02)).readEntity(TeacherWithThesesResponse.class).getId();

            Response response = request(TEACHER_PATH + "/" + t01Id, TestData.T02).delete();
            testErrorMessage(response, Response.Status.FORBIDDEN);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldReturnForbiddenToDeleteTeacherWithStudent() {
        try {
            Long t01Id = Objects.requireNonNull(createTeacher(TestData.T01)).readEntity(TeacherWithThesesResponse.class).getId();
            Long s01Id = Objects.requireNonNull(createStudent(TestData.S01)).readEntity(StudentWithThesisResponse.class).getId();

            Response response = request(TEACHER_PATH + "/" + t01Id, TestData.S01).delete();
            testErrorMessage(response, Response.Status.FORBIDDEN);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    public static Response createTeacher(TestData.Teacher teacher) {
        return createResource(TEACHER_PATH, () ->
                        CreateTeacherRequest.builder()
                                .aisId(teacher.aisId)
                                .name(teacher.name)
                                .email(teacher.email)
                                .password(Base64.getEncoder().encodeToString(teacher.password.getBytes()))
                                .department(teacher.department)
                                .institute(teacher.institute)
                                .build()
                , null);
    }


}
