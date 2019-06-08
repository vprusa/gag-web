package cz.muni.fi.gag.web.app;

<<<<<<< HEAD
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
=======
import org.apache.http.HttpStatus;
>>>>>>> UI - User, about, home; some refactoring UI and some pom.xml, sketch UI test
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.shrinkwrap.api.ShrinkWrap;
<<<<<<< HEAD
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
=======
>>>>>>> UI - User, about, home; some refactoring UI and some pom.xml, sketch UI test
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

<<<<<<< HEAD
import cz.muni.fi.gag.web.service.FingerDataLineTest;
import cz.muni.fi.gag.web.service.TestBase;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
=======
import cz.muni.fi.gag.web.service.TestBase;

>>>>>>> UI - User, about, home; some refactoring UI and some pom.xml, sketch UI test
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
<<<<<<< HEAD

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
=======
import java.io.File;
import java.net.URL;
>>>>>>> UI - User, about, home; some refactoring UI and some pom.xml, sketch UI test
import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 */
@RunWith(Arquillian.class)
public class AuthenticationTest extends TestBase {

<<<<<<< HEAD
    private static final Logger log = Logger.getLogger(AuthenticationTest.class.getSimpleName());

    private static final String KEYCLOAK_TOKEN_URL = "http://localhost:8180/auth/realms/google-identity-provider-realm/protocol/openid-connect/token";
    private static final String APP_URL = "http://localhost:8080/gagweb/";
    private static final String SECURITY_CHECK = "j_security_check";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "password";
    private static final String JSESSION_ID = "JSESSIONID";

    // curl -d "client_id=google-authentication" -d "username=test" -d
    // "password=password" -d "grant_type=password" -d "scope=openid" -d
    // "realm=Username-Password-Authentication"
    // "http://localhost:8180/auth/realms/google-identity-provider-realm/protocol/openid-connect/token"

    @ArquillianResource
    private URL baseUri;

    private String getKeycloakUrl() {
        return KEYCLOAK_TOKEN_URL;
    }

    private String getAppUrl() {
        return baseUri.toString();
    }

    private String login(String userName, String password) throws Exception {
        String url = getKeycloakUrl();
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        List urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("client_id", "google-authentication"));
        urlParameters.add(new BasicNameValuePair("username", userName));
        urlParameters.add(new BasicNameValuePair("password", password));
        urlParameters.add(new BasicNameValuePair("grant_type", "password"));
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        JsonReader reader = Json.createReader(response.getEntity().getContent());
        log.info("log message");
        JsonObject jobj = reader.readObject();
        JsonString possibleStr = jobj.getJsonString("access_token");
        if (possibleStr == null) {
            log.info(EntityUtils.toString(response.getEntity(), "UTF-8"));
            throw new NullPointerException(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }
        String accessToken = possibleStr.getString();
        return accessToken;
    }

    @SuppressWarnings("unchecked")
    public void testGenericLogIn(String username, String password) {
=======
    private static Logger log = Logger.getLogger(AuthenticationTest.class.getSimpleName());
    
    private static final String SECURITY_CHECK = "j_security_check";
    private static final String USERNAME = "j_username";
    private static final String PASSWORD = "j_password";
    private static final String JSESSION_ID = "JSESSIONID";

    @Deployment(testable = false)
    public static WebArchive createDeployment() {

        File[] files = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies()
                .resolve()
                .withTransitivity()
                .asFile();

        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addAsWebInfResource("WEB-INF/beans.xml", "beans.xml")
                .addAsWebInfResource("WEB-INF/jboss-web.xml", "jboss-web.xml")
                .addAsWebInfResource("WEB-INF/web.xml", "web.xml")
                .addAsManifestResource("MANIFEST.MF")
                .addAsLibraries(files);

        log.info(war.toString(true));

        return war;
    }


    @ArquillianResource
    private URL baseUri;

    @SuppressWarnings("unchecked")
    public void testGenericLogIn(String username, String password) {

>>>>>>> UI - User, about, home; some refactoring UI and some pom.xml, sketch UI test
        MultivaluedMap formData = new MultivaluedMapImpl();
        formData.add(USERNAME, username);
        formData.add(PASSWORD, password);

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUri + SECURITY_CHECK);
        Response response = target.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(Entity.form(new Form(formData)));

        Assert.assertEquals("User cannot log in", HttpStatus.SC_OK, response.getStatus());
        Assert.assertTrue("No session created on valid log in",
                !response.getCookies().get(JSESSION_ID).getValue().isEmpty());
<<<<<<< HEAD
        response.close();
    }

    @Deployment
    public static WebArchive deployment() {
        //return getDeployment(AuthenticationTest.class);

        File[] files = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies()
                .resolve()
                .withTransitivity()
                .asFile();

        WebArchive war = ShrinkWrap.create(WebArchive.class)
                //.addAsWebInfResource("WEB-INF/beans.xml", "WEB-INF/beans.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                //.addAsWebInfResource("WEB-INF/jboss-web.xml", "WEB-INF/jboss-web.xml")
                .addAsWebInfResource("WEB-INF/web.xml", "WEB-INF/web.xml")
                //.addAsManifestResource("MANIFEST.MF")
                .addAsLibraries(files);
        return war;
/*
        String keycloakGroupId = "org.keycloak:";
        String keycloakVersion = ":6.0.1";

            File[] files = Maven.resolver()
                    .resolve(keycloakGroupId + "keycloak-core" + keycloakVersion,
                            keycloakGroupId + "keycloak-common" + keycloakVersion,
                            keycloakGroupId + "keycloak-adapter-core" + keycloakVersion,
                            keycloakGroupId + "keycloak-adapter-spi" + keycloakVersion,
                            keycloakGroupId + "keycloak-client-registration-api" + keycloakVersion)
                    .withTransitivity().asFile();

            log.info("Dependency Files");

            for (File file : files) {
                log.info(file.getAbsolutePath());
            }

            // File[] keycloak
            return ShrinkWrap.create(WebArchive.class, AuthenticationTest.class.getSimpleName() + ".war")
                    //.addAsWebInfResource("WEB-INF/beans.xml", "beans.xml")
                    //.addAsWebInfResource("WEB-INF/jboss-web.xml", "jboss-web.xml")
                    .addAsWebInfResource("WEB-INF/web.xml", "WEB-/web.xml")
                    .addAsWebInfResource("WEB-INF/keycloak.json", "WEB-INF/keycloak.json")
                    //.addAsManifestResource("MANIFEST.MF")
                    //.addAsLibraries(files)
                    .addPackages(true, "cz.muni.fi.gag.web")
                    .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                    .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsLibraries(files);
                    */
    }

    @Test
    public void withTokenShouldSayHello() throws Exception {
        // TODO fix
        HttpClient client = new DefaultHttpClient();//ClientBuilder.newClient();
        HttpGet get = new HttpGet(getAppUrl());
        String accessToken = login(USERNAME, PASSWORD); 
        get.addHeader("Authorization", "Bearer " + accessToken);

        HttpResponse response = client.execute(get);
        //Response response = client.execute(post).request(MediaType.APPLICATION_JSON)
        //       .get();
        //assertEquals(200, response.getStatus());
        //assertEquals(200, EntityUtils.toString((HttpEntity) response.getEntity(), "UTF-8"));
        //assertEquals(200, accessToken);
=======

        response.close();
    }

    @Test
    public void testAdminLogIn() {
        testGenericLogIn("admin@musiclib.com", "pass");
    }

    @Test
    public void testUserLogIn() {
        testGenericLogIn("user1@musiclib.com", "pass");
    }

    @Test
    public void testSuperUserLogIn() {
        testGenericLogIn("superUser1@musiclib.com", "pass");
    }

    @Test
    public void invalidLogIn() {
        MultivaluedMap formData = new MultivaluedMapImpl();
        formData.add(USERNAME, "");
        formData.add(PASSWORD, "");

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUri + SECURITY_CHECK);
        Response response = target.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(Entity.form(new Form(formData)));

        Assert.assertEquals("Invalid user logged in", HttpStatus.SC_NOT_FOUND, response.getStatus());

        response.close();
>>>>>>> UI - User, about, home; some refactoring UI and some pom.xml, sketch UI test
    }

}
