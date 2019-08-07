package cz.muni.fi.gag.web.endpoint.websocket;

import org.jboss.logging.Logger;

import javax.websocket.*;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class MyEndpointClientJSONObject extends Endpoint {

    private static final Logger log = Logger.getLogger(MyEndpointClientJSONObject.class.getSimpleName());

    public static CountDownLatch latch = new CountDownLatch(1);
    public static String JSON = "{\"a\" : \"b\"}";
    public static String response;

    @OnOpen
    public void onOpen(Session session) {
        log.info("onOpen");
        /*try {
            session.getBasicRemote().sendText(JSON);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }*/

    }

    @OnMessage
    public void processMessage(String message) {
        log.info("processMessage");
        log.info(message);
        response = message;
        latch.countDown();
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
          final RemoteEndpoint.Basic remote = session.getBasicRemote();
          session.addMessageHandler(new MessageHandler.Whole<String>() {
              public void onMessage(String text) {
                  /*try {
                      remote.sendText("Got your message (" + text + "). Thanks !");
                  } catch (IOException ioe) {
                  }*/
                  log.info("onMessage");
                  log.info(text);
              }
          });
      }

}
