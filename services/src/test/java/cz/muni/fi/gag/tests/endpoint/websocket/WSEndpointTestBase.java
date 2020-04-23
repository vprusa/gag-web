package cz.muni.fi.gag.tests.endpoint.websocket;

import cz.muni.fi.gag.tests.endpoint.AuthenticationTestBase;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
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
public abstract class WSEndpointTestBase extends AuthenticationTestBase {

    private static final Logger log = Logger.getLogger(WSEndpointTestBase.class.getSimpleName());


    /**
     * Method used to supply connection to the server by passing the naming of
     * the websocket endpoint
     *
     * @return
     * @throws DeploymentException
     * @throws IOException
     * @throws URISyntaxException
     */
    public Session connectToServer(final Class endpointClass, final String uriStr) throws Exception {
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
        log.info("Connecting to URI: " + uriStr);
        URI uri = new URI(uriStr);
        Session session = container.connectToServer(endpointClass, clientEndpointConfig, uri);
        return session;
    }

}
