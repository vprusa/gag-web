package cz.muni.fi.gag.tests.endpoint;

import cz.muni.fi.gag.tests.common.TestEndpointBase;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URL;
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

    private static final String KEYCLOAK_AUTH_URL = "http://localhost:8180/auth/";
    private static final String KEYCLOAK_TOKEN_URL =
            KEYCLOAK_AUTH_URL + "realms/google-identity-provider-realm/protocol/openid-connect/token";
    protected static final String URL_NO_PROTOCOL = "localhost:8080/";
    protected static final String APP_URL_NO_PROTOCOL = URL_NO_PROTOCOL + "/gagweb/";
    protected static final String APP_URL = "http://" + APP_URL_NO_PROTOCOL;
    private static final String SECURITY_CHECK = "j_security_check";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "password";
    private static final String JSESSION_ID = "JSESSIONID";

    // curl -d "client_id=google-authentication" -d "username=test" -d
    // "password=password" -d "grant_type=password" -d "scope=openid" -d
    // "realm=Username-Password-Authentication"
    // "http://localhost:8180/auth/realms/google-identity-provider-realm/protocol/openid-connect/token"

    @ArquillianResource
    private URL baseURL;

    private String getKeycloakUrl() {
        return KEYCLOAK_TOKEN_URL;
    }

    public String getAppUrl() {
        if(baseURL != null) {
            log.info("baseUrl: " + baseURL.toString());
            return baseURL.toString();
        }
        return APP_URL;
    }

    private String login(String userName, String password) throws IOException {
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
        //log.info("Response entity content len");
        //log.info(response.getEntity().getContentLength());
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

    public String basicLogin() throws IOException, UnsupportedEncodingException {
        return login(USERNAME,PASSWORD);
    }

    // code below is workaround .. more info at startKC()
    public static class KCExecuted implements  Runnable {

        public volatile boolean KCStartedFlag = false;

        Process process;
        //Keycloak 6.0.1 (WildFly Core 8.0.0.Final) started in 18
        String waitedRegex = ".*Keycloak.* started in .*";
        String execDirPath = System.getProperties().get("basedir") + "/../keycloak-6.0.1/bin/";
        String execCmd = "./standalone.sh " +
                "-Djboss.socket.binding.port-offset=100 " +
                "-Dkeycloak.migration.action=import " +
                "-Dkeycloak.migration.provider=singleFile " +
                "-Dkeycloak.migration.file=" +
                System.getProperties().get("basedir") + "/../config/google-identity-provider-realm.json " +
                "-Dkeycloak.migration.strategy=OVERWRITE_EXISTING";

        @Override
        public void run() {
            log.info(execDirPath);
            log.info(execCmd);
            try {
                process = Runtime.getRuntime().exec(
                        execCmd, null, new File(execDirPath));

                //StringBuilder output = new StringBuilder();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                String line;
                // TODO custom waitFor ....
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                    if(line.matches(waitedRegex)){
                        log.info("Successfully found line matching regex: " + waitedRegex);
                        KCStartedFlag = true;
                    }
                }

                int exitVal = process.waitFor();
                if (exitVal == 0) {
                    log.info("KC ended successfully!");
                } else {
                    log.info("KC ended abnormally!");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startKC(){
        // programmatically because maven sucks and combination with WF instance and KC instance kills
        // KC when WF starts ... stupid...

        Client client = ClientBuilder.newClient();
        try {
            WebTarget target = client.target(KEYCLOAK_AUTH_URL);
            Response response = target.request().get();
//            if (response.getStatusInfo().getStatusCode() == 200) {
            if (response.getStatus() == 200) {
                log.info("KC already running so lets NOT start it again.");
                return;
            }
        //}catch(ConnectException ex){
        }catch(Exception ex){
        }

        kc = new KCExecuted();
        kct = new Thread(kc);
        kct.start();
        while(!kc.KCStartedFlag) {
            try {
                Thread.sleep(1000);
                //log.info("Waiting for KC to start...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static KCExecuted kc;
    public static Thread kct;

    @BeforeClass
    public static void beforeClass(){
        startKC();
    }

    @AfterClass
    public static void afterClass(){
        if( kct != null ) {
            kct.interrupt();
        }
    }

}

