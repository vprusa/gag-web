package cz.gag.tests.recognition.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gag.tests.common.FileLogger;
import cz.gag.tests.endpoint.GestureEndpointTest;
import cz.gag.tests.endpoint.websocket.WSEndpointTestBase;
import cz.gag.web.persistence.entity.Gesture;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.websocket.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class AutoRecognizerWSEndpointTest extends WSRecognitionEndpointTest /*WSEndpointTestBase*/ {

    private static final Logger log = FileLogger.getLogger(AutoRecognizerWSEndpointTest.class.getSimpleName());
    private static final String TESTED_ENDPOINT = "ws://" + URL_NO_PROTOCOL + "gagweb/ws/autoRecognizer";

    @Deployment(name = "autoRecognizerDeployment")
//    @Deployment
    public static WebArchive deployment() {
        return getDeployment(AutoRecognizerWSEndpointTest.class);
    }

    public static class AutoRecognizerClient extends Endpoint {

        private Session session;
        private CountDownLatch latch;
        private String response;

        public AutoRecognizerClient(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            this.session = session;
            session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                log.info("Received message: " + message);
                response = message;
                latch.countDown();
            });
        }

        public void sendText(String msg) throws Exception {
            session.getBasicRemote().sendText(msg);
        }

        public String getResponse() {
            return response;
        }
    }
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @RunAsClient
    public void testAutoGestureRecognition() throws Exception {
        // Use same gesture as both target and replay to guarantee match
        Long gestureId = 79L;
        HttpClient httpClient = new DefaultHttpClient();

        Gesture gRef = GestureEndpointTest.getGesture(gestureId, httpClient, basicLogin());
        assertNotNull("Gesture must not be null", gRef);

        CountDownLatch latch = new CountDownLatch(1);
        AutoRecognizerClient client = new AutoRecognizerClient(latch);

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        URI uri = new URI(TESTED_ENDPOINT);
        ClientEndpointConfig config = getEndpointConfig();
        container.connectToServer(client, config, uri);


        HashMap map = new HashMap();
        map.put("targetGestureId", gestureId);
        map.put("gestureToReplayId", gestureId);

        String payload = objectMapper.writeValueAsString(map);

        log.info("Sending recognition request: " + payload);
        client.sendText(payload);

        boolean success = latch.await(20, TimeUnit.SECONDS);
        assertNotNull("WebSocket response timed out or was null", client.getResponse());
        log.info("Recognition response: " + client.getResponse());
    }
}
