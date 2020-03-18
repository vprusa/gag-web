package cz.muni.fi.gag.tests.endpoint.websocket;

import cz.muni.fi.gag.tests.endpoint.AuthenticationTestBase;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.HandshakeResponse;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class WSDataLineEndpointTest extends AuthenticationTestBase {

    private static final Logger log = Logger.getLogger(WSDataLineEndpointTest.class.getSimpleName());

    public static final String TESTED_ENDPOINT = "ws://" + URL_NO_PROTOCOL + WSDataLineEndpointTest.class.getSimpleName() + "/datalinews";

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(WSDataLineEndpointTest.class);
    }

    @Test
    @RunAsClient
    public void testEndpointJSONObject() throws Exception {
        Session session = connectToServer();
        log.info(session);
        boolean latchWait = WSEndpointClientJSONObject.latch.await(3, TimeUnit.SECONDS);
        log.info(latchWait);
        log.info(WSEndpointClientJSONObject.response);

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
                    log.info(accessToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterResponse(HandshakeResponse hr) {
                super.afterResponse(hr);
                log.info(hr.toString());
            }
        };
        ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        log.info("Connecting to URI: " + TESTED_ENDPOINT);
        URI uri = new URI(TESTED_ENDPOINT);
        Session session = container.connectToServer(WSEndpointClientJSONObject.class, clientEndpointConfig, uri);
        MessageHandler.Whole<String> mhw = new MessageHandler.Whole<String>() {

            @Override
            public void onMessage(String text) {
                  /*try {
                      remote.sendText("Got your message (" + text + "). Thanks !");
                  } catch (IOException ioe) {
                  }*/
                //log.info("onMessage");
                log.info(text);
                WSEndpointClientJSONObject.response.add(text);
            }

        };
        session.addMessageHandler(mhw);
        return session;
    }

}
