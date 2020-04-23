package cz.muni.fi.gag.tests.endpoint.websocket;

import org.jboss.logging.Logger;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Vojtech Prusa
 *
 */
public class WSEndpointClientJSONObject extends Endpoint {

    private static final Logger log = Logger.getLogger(WSEndpointClientJSONObject.class.getSimpleName());

    public static CountDownLatch latch = new CountDownLatch(1);
    public static String JSON = "{\"action\" : \"replay\", \"gestureId\":2}";
    public static List<String> response;

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        response = new ArrayList<String>();
        log.info("WSEndpointClientJSONObject.onOpen");
        final RemoteEndpoint.Basic remote = session.getBasicRemote();
        try {
            remote.sendText(JSON);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

      @Override
      public void onError(Session session, Throwable thr) {
        log.info(thr.getMessage());
      }

}
