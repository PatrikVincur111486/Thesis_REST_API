package sk.stuba.fei.uim.vsa.pr2.utils;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import sk.stuba.fei.uim.vsa.pr2.Project2Application;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.MessageResponse;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr2.ApplicationConfiguration.BASE_URI;
import static sk.stuba.fei.uim.vsa.pr2.utils.TestConstants.*;
import static sk.stuba.fei.uim.vsa.pr2.utils.TestData.OBJECT_CONTENT_LENGTH;
import static sk.stuba.fei.uim.vsa.pr2.utils.TestUtils.clearDB;
import static sk.stuba.fei.uim.vsa.pr2.utils.TestUtils.getDBConnection;

@Slf4j
public abstract class ResourceTest {

    public static final Boolean BONUS_CHECK = Boolean.parseBoolean(System.getenv("BONUS_CHECK_ENABLED"));

    protected static HttpServer server;
    protected static WebTarget client;
    protected static Connection db;
    protected static EntityManagerFactory emf;

    @BeforeAll
    public static void initClient() throws SQLException, ClassNotFoundException {
        log.info("Starting HTTP server for testing");
        server = Project2Application.startServer();
        log.info("Creating client for HTTP server");
        client = ClientBuilder.newClient().target(BASE_URI);
        log.info("Client created " + client.toString());
        db = getDBConnection(DB_URL, USERNAME, PASSWORD, DRIVER);
        assertNotNull(db);
        emf = Persistence.createEntityManagerFactory("vsa-project-2");
        assertNotNull(emf);
        assertTrue(server.isStarted());
    }

    @BeforeEach
    void inBetween() {
        clearDB(db);
        emf.getCache().evictAll();
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        log.info("Cleaning after the test");
        emf.close();
        db.close();
        client = null;
        server.shutdownNow();
    }

    public static Invocation.Builder request(String path) {
        return request(path, null, null, null);
    }

    public static Invocation.Builder request(String path, TestData.User authUser) {
        if (authUser == null) {
            return request(path);
        }
        return request(path, authUser.email, authUser.password, null);
    }

    public static Invocation.Builder request(String path, TestData.User authUser, Map<String, Object> queryParams) {
        if (authUser == null) {
            return request(path, null, null, queryParams);
        }
        return request(path, authUser.email, authUser.password, queryParams);
    }

    public static Invocation.Builder request(String path, String username, String password, Map<String, Object> queryParams) {
        WebTarget target = client.path(path);
        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, Object> param : queryParams.entrySet()) {
                target = target.queryParam(param.getKey(), param.getValue());
            }
        }
        Invocation.Builder builder = target.request().accept(MediaType.APPLICATION_JSON_TYPE);
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            builder.header(HttpHeaders.AUTHORIZATION, buildBasicAuthorizationHeader(username, password));
        }
        return builder;
    }

    public static String buildBasicAuthorizationHeader(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    public static <T> Response createResource(String path, Supplier<T> entitySupplier, TestData.User authUser) {
        try {
            Response response = request(path, authUser)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(entitySupplier.get(),
                            MediaType.APPLICATION_JSON));
            assertNotNull(response);
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
            return null;
        }
    }

    public static boolean isBonusImplemented() {
        try (Response response = request("/application.wadl").accept(MediaType.APPLICATION_XML_TYPE).get()) {
            if (response.getStatus() != Response.Status.OK.getStatusCode()) return false;
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            String body = response.readEntity(String.class);
            Document xml = builder.parse(new InputSource(new StringReader(body)));
            XPath xpath = XPathFactory.newInstance().newXPath();

            Node searchNode = (Node) xpath.compile("/application/resources/resource[@path='/search']").evaluate(xml, XPathConstants.NODE);
            if (searchNode == null) return false;
            Node thesesNode = (Node) xpath.compile("./resource[@path='/theses']").evaluate(searchNode, XPathConstants.NODE);
            if (thesesNode == null) return false;
            NodeList thesesParamNodes = (NodeList) xpath.compile("./method//param").evaluate(thesesNode, XPathConstants.NODESET);
            int params = thesesParamNodes.getLength();
            if (params != 2) return false;
            boolean theseHasPage = false;
            boolean theseHasSize = false;
            for (int i = 0; i < thesesParamNodes.getLength(); i++) {
                Node n = thesesParamNodes.item(i);
                if (n.hasAttributes()) {
                    Node a = n.getAttributes().getNamedItem("name");
                    if (a.getNodeValue().equalsIgnoreCase("page")) theseHasPage = true;
                    if (a.getNodeValue().equalsIgnoreCase("size")) theseHasSize = true;
                }
            }
            if (!theseHasPage || !theseHasSize) return false;

            Node studentsNode = (Node) xpath.compile("./resource[@path='/students']").evaluate(searchNode, XPathConstants.NODE);
            if (studentsNode == null) return false;
            Node teachersNode = (Node) xpath.compile("./resource[@path='/teachers']").evaluate(searchNode, XPathConstants.NODE);
            if (teachersNode == null) return false;
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static void testErrorMessage(Response response, Response.Status... status) {
        try {
            assertNotNull(response);
            assertNotEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(Arrays.stream(status).anyMatch(s -> s.getStatusCode() == response.getStatus()));
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            MessageResponse message = response.readEntity(MessageResponse.class);
            assertNotNull(message);
            log.info("Received message: " + message);
            assertNotNull(message.getCode());
            assertTrue(Arrays.stream(status).anyMatch(s -> s.getStatusCode() == message.getCode()));
            assertNotNull(message.getMessage());
            assertFalse(message.getMessage().isEmpty());
            log.info("Content of the error message: " + message.getMessage());
            log.info("Has error object: " + (message.getError() != null));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

}
