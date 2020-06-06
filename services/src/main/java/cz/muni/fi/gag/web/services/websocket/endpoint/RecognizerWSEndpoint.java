package cz.muni.fi.gag.web.services.websocket.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.persistence.entity.User;
import cz.muni.fi.gag.web.services.logging.Log;
import cz.muni.fi.gag.web.services.recognition.GestureMatcher;
import cz.muni.fi.gag.web.services.rest.endpoint.BaseEndpoint;
import cz.muni.fi.gag.web.services.service.DataLineService;
import cz.muni.fi.gag.web.services.service.GestureService;
import cz.muni.fi.gag.web.services.service.UserService;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.Action;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.ActionDecoder;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.PlayerActions;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.RecognitionActions;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.datalines.*;
import cz.muni.fi.gag.web.services.websocket.service.DataLineRePlayer;
import cz.muni.fi.gag.web.services.websocket.service.GestureRecognizer;
import org.jboss.logging.Logger;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * @author Vojtech Prusa
 * <p>
 * TODO
 * split this to 3 endpoints
 * 1. record
 * 2. replay
 * 3. recognize
 * also consider how to make it possible to combine 1. & 3.
 */
@SessionScoped
@ServerEndpoint(value = "/ws/recognizer",
        encoders = {
                DataLineEncoder.class, FingerDataLineEncoder.class, WristDataLineEncoder.class
        },
        decoders = {},
        configurator = CustomServerEndpointConfiguration.class
)
public class RecognizerWSEndpoint extends BaseWSEndpoint {

    public static final Logger log = Logger.getLogger(RecognizerWSEndpoint.class.getSimpleName());

    private static final String REPLAYER_KEY = "replayer";
//    private static final String RECOGNITION_KEY = "recognition";

    @Inject
    private DataLineService dataLineService;

    @Inject
    private GestureService gestureService;

    @Inject
    private UserService userService;

    @Inject
    private DataLineRePlayer rep;

    @Inject
    private GestureRecognizer rec;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        //sessionService.addSession(session);
        Principal userPrincipal = (Principal) config.getUserProperties().get("UserPrincipal");
        User current = BaseEndpoint.current(userPrincipal, userService);
        // TODO if user not in role stop! (with sufficient error message)
    }

    @OnClose
    public void onClose(Session session) {
        Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
        if (replayer != null) {
            replayer.interrupt(); // stop();
        }
    }

    // TODO add decoders as custom bean injections?
    private DataLineDecoder dld = new DataLineDecoder();
    private FingerDataLineDecoder fdld = new FingerDataLineDecoder();
    private WristDataLineDecoder wdld = new WristDataLineDecoder();
    private ActionDecoder<Action> ad = new ActionDecoder(Action.class);
    private ActionDecoder<PlayerActions> pad = new ActionDecoder(PlayerActions.class, Action.ActionsTypesEnum.PLAYER);
    private ActionDecoder<RecognitionActions> rad = new ActionDecoder(RecognitionActions.class, Action.ActionsTypesEnum.RECOGNITION);

    private ObjectMapper objectMapper = new ObjectMapper();

    // https://docs.oracle.com/middleware/12213/wls/WLPRG/websockets.htm#WLPRG1000
    @OnMessage
    public void onMessage(String msg, Session session) {
        log.info("RecognizerWSEndpoint.onMessage");
        log.info(msg.toString());

        DataLine dl = null;
        if (rec.isRecognizing()) {
            if (fdld.willDecode(msg)) {
//                MFingerDataLine dl = fdld.decode(msg);
                dl = fdld.decode(msg);
            } else if (wdld.willDecode(msg)) {
//                MWristDataLine dl = wdld.decode(msg);
                dl = wdld.decode(msg);
            } else if (dld.willDecode(msg)) {
//                MDataLine dl = dld.decode(msg);
                dl = dld.decode(msg);
            }
        }

        if (dl != null && rec.isRecognizing()) {
            log.info("isRecognizing");
            List<GestureMatcher> lgm = rec.recognize(dl);
            if (!lgm.isEmpty()) {
                log.info("sendObject(lgm)");
                log.info(lgm);
                try {
                    session.getBasicRemote().sendText(objectMapper.writeValueAsString(lgm));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        // no dataline matched ( dl == null && ... )
        } else if (ad.willDecode(msg)) {
            log.info("ad.decode(msg)");
            log.info(msg);
            if (rad.willDecode(msg)) {
                log.info("rad");
                RecognitionActions ra = (RecognitionActions) rad.decode(msg);
                switch (ra.getAction()) {
                    case START: {
                        log.info("RECOGNITION - START");
                        Principal userPrincipal = (Principal) session.getUserProperties().get("UserPrincipal");
                        log.info("userPrincipal");
                        log.info(userPrincipal);
                        User current = BaseEndpoint.current(userPrincipal, userService);
                        log.info("current");
                        log.info(current);
                        rec.start(current);
                        try {
                            // Send ACK
                            session.getBasicRemote().sendText(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case STOP: {
                        // TODO fix,
                        if (rec != null) {
                            rec.stop();
                        }
                        break;
                    }
                }
            }
        } else {
            if (!rec.isRecognizing()) {
                log.info("Unknown message: " + msg);
            }
        }
    }

    // Exception handling
    @OnError
    public void onError(Session session, Throwable t) {
        Log.info("onError");
        log.info(getClass().getSimpleName());
        t.printStackTrace();
        if (rec != null) {
            rec.stop();
        }
    }

}
