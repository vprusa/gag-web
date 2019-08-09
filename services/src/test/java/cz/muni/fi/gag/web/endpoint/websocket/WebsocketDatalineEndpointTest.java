package cz.muni.fi.gag.web.endpoint.websocket;

import cz.muni.fi.gag.web.endpoint.AuthenticationTestBase;
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
import java.util.concurrent.TimeUnit;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class WebsocketDatalineEndpointTest extends AuthenticationTestBase {

    private static final Logger log = Logger.getLogger(WebsocketDatalineEndpointTest.class.getSimpleName());

    public static final String TESTED_ENDPOINT = "ws://" + APP_URL_NO_PROTOCOL + "datalinews";

    @Test
    @RunAsClient
    public void testEndpointEmptyJSONObject() throws Exception {
        Session session = connectToServer();
        log.info(session);
        boolean latchWait = MyEndpointClientJSONObject.latch.await(3, TimeUnit.SECONDS);
        log.info(latchWait);
        log.info(MyEndpointClientJSONObject.response);

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
        URI uri = new URI(TESTED_ENDPOINT);
        Session session = container.connectToServer(MyEndpointClientJSONObject.class, clientEndpointConfig, uri);
        MessageHandler.Whole<String> mhw = new MessageHandler.Whole<String>() {

            @Override
            public void onMessage(String text) {
                  /*try {
                      remote.sendText("Got your message (" + text + "). Thanks !");
                  } catch (IOException ioe) {
                  }*/
                //log.info("onMessage");
                log.info(text);
                MyEndpointClientJSONObject.response.add(text);
            }

        };
        session.addMessageHandler(mhw);
        return session;
    }

}
