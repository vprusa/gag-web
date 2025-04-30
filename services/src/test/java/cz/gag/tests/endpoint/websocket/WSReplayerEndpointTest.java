package cz.gag.tests.endpoint.websocket;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class WSReplayerEndpointTest extends WSEndpointTestBase {

    private static final Logger log = Logger.getLogger(WSReplayerEndpointTest.class.getSimpleName());

    public static final String TESTED_ENDPOINT = "ws://" + URL_NO_PROTOCOL + WSReplayerEndpointTest.class.getSimpleName() + "/datalinews";

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(WSReplayerEndpointTest.class);
    }

    @Test
    @RunAsClient
    public void testEndpointJSONObject() throws Exception {
        Session session = connectToServer(WSEndpointClientJSONObject.class, TESTED_ENDPOINT);
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

        return session;
    }

}
