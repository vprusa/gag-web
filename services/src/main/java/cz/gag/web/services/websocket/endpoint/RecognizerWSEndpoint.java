package cz.gag.web.services.websocket.endpoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gag.web.persistence.entity.DataLine;
import cz.gag.web.persistence.entity.User;
import cz.gag.web.services.logging.Log;
import cz.gag.web.services.recognition.matchers.MultiSensorGestureMatcher;
import cz.gag.web.services.rest.endpoint.BaseEndpoint;
import cz.gag.web.services.service.DataLineService;
import cz.gag.web.services.service.GestureService;
import cz.gag.web.services.service.UserService;
import cz.gag.web.services.websocket.endpoint.config.CustomServerEndpointConfiguration;
import cz.gag.web.services.websocket.endpoint.packet.actions.Action;
import cz.gag.web.services.websocket.endpoint.packet.actions.ActionDecoder;
import cz.gag.web.services.websocket.endpoint.packet.actions.PlayerActions;
import cz.gag.web.services.websocket.endpoint.packet.actions.RecognitionActions;
import cz.gag.web.services.websocket.endpoint.packet.datalines.*;
import cz.gag.web.services.websocket.service.GestureRecognizer;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static cz.gag.web.services.websocket.endpoint.ReplayerWSEndpoint.REPLAYER_KEY;

/**
 * @author Vojtech Prusa
 */
@SessionScoped
@ServerEndpoint(value = "/ws/recognizer",
        encoders = {
                DataLineEncoder.class, FingerDataLineEncoder.class, WristDataLineEncoder.class
        },
        decoders = {},
        configurator = CustomServerEndpointConfiguration.class
)
public class RecognizerWSEndpoint /*extends BaseWSEndpoint*/ {

    public static final Log.TypedLogger log = new Log.TypedLogger<Log.LoggerTypeWSRecognizer>(Log.LoggerTypeWSRecognizer.class);

    @Inject
    private DataLineService dataLineService;

    @Inject
    private GestureService gestureService;

    @Inject
    private UserService userService;

//    @Inject
//    private DataLineRePlayer rep;

    @Inject
    private GestureRecognizer rec;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        // sessionService.addSession(session);
        Principal userPrincipal = (Principal) config.getUserProperties().get("UserPrincipal");
        User current = BaseEndpoint.current(userPrincipal, userService);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
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
    private DataLineDecoder bdlDecoder = new DataLineDecoder();
    private FingerDataLineDecoder fdlDecoder = new FingerDataLineDecoder();
    private WristDataLineDecoder wdlDecoder = new WristDataLineDecoder();
    private ActionDecoder<Action> actionDecoder = new ActionDecoder(Action.class);
    private ActionDecoder<PlayerActions> playerActionDecoder = new ActionDecoder(PlayerActions.class, Action.ActionsTypesEnum.PLAYER);
    private ActionDecoder<RecognitionActions> recognitionActionDecoder =
            new ActionDecoder(RecognitionActions.class, Action.ActionsTypesEnum.RECOGNITION);
//    private RecognizedGestureEncoder rge = new RecognizedGestureEncoder();

    private ObjectMapper objectMapper = new ObjectMapper();

    // https://docs.oracle.com/middleware/12213/wls/WLPRG/websockets.htm#WLPRG1000
    @OnMessage
    public void onMessage(String msg, Session session) {
        log.info("RecognizerWSEndpoint.onMessage");
        log.info(msg.toString());

        DataLine dl = null;
        if (rec.isRecognizing()) {
            if (fdlDecoder.willDecode(msg)) {
//                MFingerDataLine dl = fdld.decode(msg);
                dl = fdlDecoder.decode(msg);
            } else if (wdlDecoder.willDecode(msg)) {
//                MWristDataLine dl = wdld.decode(msg);
                dl = wdlDecoder.decode(msg);
            } else if (bdlDecoder.willDecode(msg)) {
//                MDataLine dl = dld.decode(msg);
                dl = bdlDecoder.decode(msg);
            }
        }

        if (dl != null && rec.isRecognizing()) {
            log.info("isRecognizing");
            List<MultiSensorGestureMatcher> lgm = rec.recognize(dl);
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
        } else if (actionDecoder.willDecode(msg)) {
            log.info("ad.decode(msg)");
            log.info(msg);
            if (recognitionActionDecoder.willDecode(msg)) {
                log.info("rad");
                RecognitionActions ra = (RecognitionActions) recognitionActionDecoder.decode(msg);
                switch (ra.getAction()) {
                    case START: {
                        log.info("RECOGNITION - START");
                        Principal userPrincipal = (Principal) session.getUserProperties().get("UserPrincipal");
                        log.info("userPrincipal: ");
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
