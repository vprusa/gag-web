package cz.muni.fi.gag.web.endpoint.websocket;

import org.jboss.logging.Logger;

import javax.websocket.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

//@ClientEndpoint
public class MyEndpointClientJSONObject extends Endpoint {

    private static final Logger log = Logger.getLogger(MyEndpointClientJSONObject.class.getSimpleName());

    public static CountDownLatch latch = new CountDownLatch(1);
    public static String JSON = "{\"action\" : \"replayGesture\", \"gestureId\":2}";
    public static String response;

    @Override
    public void onOpen(Session session, EndpointConfig config) {
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
