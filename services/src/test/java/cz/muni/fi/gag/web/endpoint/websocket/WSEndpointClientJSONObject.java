package cz.muni.fi.gag.web.endpoint.websocket;

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
    public static String JSON = "{\"action\" : \"replayGesture\", \"gestureId\":2}";
    public static List<String> response;

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        response = new ArrayList<String>();
        final RemoteEndpoint.Basic remote = session.getBasicRemote();
        //session.addMessageHandler(mhw);
        try {
            session.getBasicRemote().sendText(JSON);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

      @Override
      public void onError(Session session, Throwable thr) {
        log.info(thr.getMessage());
      }

}
