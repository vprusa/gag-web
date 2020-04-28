package cz.muni.fi.gag.tests.recognition.websocket;

import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.ActionEncoderBase;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.RecognitionActions;
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
public class WSRecognitionClientEndpoint extends Endpoint {

    private static final Logger log = Logger.getLogger(WSRecognitionClientEndpoint.class.getSimpleName());

    public static CountDownLatch latch = new CountDownLatch(1);
    public static List<String> response = new ArrayList<String>();
    private Gesture gRef;

    public WSRecognitionClientEndpoint(Gesture gRef) {
        this.gRef = gRef;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        log.info("WSRecognitionClientEndpoint.onOpen");
        response = new ArrayList<String>(); // re-init
        final RemoteEndpoint.Basic remote = session.getBasicRemote();
        //session.addMessageHandler(mhw);
        try {
            ActionEncoderBase aeb = new ActionEncoderBase(RecognitionActions.class);
            RecognitionActions ra = new RecognitionActions();
            ra.setAction(RecognitionActions.ActionsEnum.START);
            log.info("Action ra");
            log.info(ra.toString());
            String raStr = aeb.encode(ra);
            session.getBasicRemote().sendText(raStr);

            /*
            DataLineEncoder dle = new DataLineEncoder();
            FingerDataLineEncoder fdle = new FingerDataLineEncoder();
            WristDataLineEncoder wdle = new WristDataLineEncoder();

            List<DataLine> dll = gRef.getData();
            Iterator<DataLine> dlli = dll.iterator();
            while (dlli.hasNext()) {
                DataLine dl = dlli.next();
                log.info("dl type: " + dl.getClass() + " data: " + dl.toString());
                try {
                    if (dl instanceof FingerDataLine) {
                        session.getBasicRemote().sendText(fdle.encode((FingerDataLine) dl));
                    } else if (dl instanceof WristDataLine) {
                        session.getBasicRemote().sendText(wdle.encode((WristDataLine) dl));
                    } else {
                        session.getBasicRemote().sendText(dle.encode(dl));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (EncodeException e) {
                    e.printStackTrace();
                }
            }
            */
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

      @Override
      public void onError(Session session, Throwable thr) {
        log.info(thr.getMessage());
      }

}
