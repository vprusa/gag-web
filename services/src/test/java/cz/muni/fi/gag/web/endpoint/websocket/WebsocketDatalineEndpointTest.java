package cz.muni.fi.gag.web.endpoint.websocket;

import cz.muni.fi.gag.web.endpoint.AuthenticationTestBase;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class WebsocketDatalineEndpointTest extends AuthenticationTestBase {

    private static final Logger log = Logger.getLogger(WebsocketDatalineEndpointTest.class.getSimpleName());

    public static final String TESTED_ENDPOINT = "ws://" + APP_URL_NO_PROTOCOL + "datalinews";

    // curl -d "client_id=google-authentication" -d "username=test" -d
    // "password=password" -d "grant_type=password" -d "scope=openid" -d
    // "realm=Username-Password-Authentication"
    // "http://localhost:8180/auth/realms/google-identity-provider-realm/protocol/openid-connect/token"
   // @Test
   // @RunAsClient
    //public void authenticateUser(@ArquillianResteasyResource final WebTarget webTarget) throws Exception {
    public void testEndpoint() throws Exception {
        HttpClient client = new DefaultHttpClient();//ClientBuilder.newClient();
        String accessToken = basicLogin();

        HttpPost insertPost = new HttpPost(TESTED_ENDPOINT);
        insertPost.addHeader("Authorization", "Bearer " + accessToken);
    }

    @Test
    @RunAsClient
    public void testEndpointEmptyJSONObject() throws Exception {
        String JSON = "{\"a\" : \"b\"}";
        Session session = connectToServer();
        //assertNotNull(session);
        log.info(session);
        //assertTrue(MyEndpointClientJSONObject.latch.await(2, TimeUnit.SECONDS));
        //assertEquals(JSON, MyEndpointClientJSONObject.response);
    }

    /**
     * Method used to supply connection to the server by passing the naming of
     * the websocket endpoint
     *
     * @return
     * @throws DeploymentException
     * @throws IOException
     * @throws URISyntaxException
     */
    public Session connectToServer() throws Exception {
        log.info("connectToServer");
        // https://www.programcreek.com/java-api-examples/?api=javax.websocket.ClientEndpointConfig

        ClientEndpointConfig.Configurator configurator = new ClientEndpointConfig.Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {

                try {
                    String accessToken = basicLogin();
                    headers.put("Authorization", Arrays.asList("Bearer " + accessToken));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //HttpPost insertPost = new HttpPost(TESTED_ENDPOINT);
        //insertPost.addHeader("Authorization", "Bearer " + accessToken);
        ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create().configurator(configurator).build();

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        URI uri = new URI(TESTED_ENDPOINT);

        Session session = container.connectToServer(MyEndpointClientJSONObject.class, clientEndpointConfig, uri);

        return session;
    }


}
