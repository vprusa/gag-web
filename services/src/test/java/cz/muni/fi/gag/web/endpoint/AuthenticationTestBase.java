package cz.muni.fi.gag.web.endpoint;

import cz.muni.fi.gag.web.common.TestEndpointBase;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vojtech Prusa
 *
 * This test expects that:
 * - app is running on WF at {@link AuthenticationTestBase#APP_URL}
 * - KC is configured and running with TOKEN endpoint at {@link AuthenticationTestBase#KEYCLOAK_TOKEN_URL}
 * - has some arguments set properly
 * -- {@link AuthenticationTestBase#JSESSION_ID}
 * -- {@link AuthenticationTestBase#SECURITY_CHECK}
 * -- {@link AuthenticationTestBase#USERNAME}
 * -- {@link AuthenticationTestBase#PASSWORD}
 *
 * {@link AuthenticationTest}
 */
public abstract class AuthenticationTestBase extends TestEndpointBase {

    private static final Logger log = Logger.getLogger(AuthenticationTestBase.class.getSimpleName());

    private static final String KEYCLOAK_TOKEN_URL =
            "http://localhost:8180/auth/realms/google-identity-provider-realm/protocol/openid-connect/token";
    protected static final String APP_URL_NO_PROTOCOL = "localhost:8080/gagweb/";
    protected static final String APP_URL = "http://" + APP_URL_NO_PROTOCOL;
    private static final String SECURITY_CHECK = "j_security_check";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "password";
    private static final String JSESSION_ID = "JSESSIONID";

    // curl -d "client_id=google-authentication" -d "username=test" -d
    // "password=password" -d "grant_type=password" -d "scope=openid" -d
    // "realm=Username-Password-Authentication"
    // "http://localhost:8180/auth/realms/google-identity-provider-realm/protocol/openid-connect/token"

    // @ArquillianResource
    //private URL baseUri;

    private String getKeycloakUrl() {
        return KEYCLOAK_TOKEN_URL;
    }

    public String getAppUrl() {
        return APP_URL;//baseUri.toString();
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
        log.info("Login response");
        log.info(response);
        log.info("Response entity content len");
        log.info(response.getEntity().getContentLength());
        //response.getEntity().getContent();
        JsonReader reader = Json.createReader(response.getEntity().getContent());
        JsonObject jobj = reader.readObject();
        JsonString possibleStr = jobj.getJsonString("access_token");
        if (possibleStr == null) {
            log.info("PossibleStr is null so printing response entity");
            log.info(EntityUtils.toString(response.getEntity(), "UTF-8"));
            throw new NullPointerException(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }
        String accessToken = possibleStr.getString();
        log.info("AccessToken: " + accessToken);
        return accessToken;
    }

    public String basicLogin() throws Exception {
        return login(USERNAME,PASSWORD);
    }

    @SuppressWarnings("unchecked")
    public void testGenericLogIn(String username, String password) {
       /* MultivaluedMap formData = new MultivaluedMapImpl();
        formData.add(USERNAME, username);
        formData.add(PASSWORD, password);

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUri + SECURITY_CHECK);
        Response response = target.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(Entity.form(new Form(formData)));

        Assert.assertEquals("User cannot log in", HttpStatus.SC_OK, response.getStatus());
        Assert.assertTrue("No session created on valid log in",
                !response.getCookies().get(JSESSION_ID).getValue().isEmpty());
        response.close();

        */
    }

}
