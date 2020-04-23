package cz.muni.fi.gag.web.services.websocket.endpoint;

import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.persistence.entity.User;
import cz.muni.fi.gag.web.services.logging.Log;
import cz.muni.fi.gag.web.services.mapped.MDataLine;
import cz.muni.fi.gag.web.services.mapped.MFingerDataLine;
import cz.muni.fi.gag.web.services.mapped.MWristDataLine;
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
 */
@SessionScoped
@ServerEndpoint(value = "/datalinews",
        encoders = {
                // JsonEncoder.class
                DataLineEncoder.class, FingerDataLineEncoder.class, WristDataLineEncoder.class
        },
        decoders = {},
        configurator = CustomServerEndpointConfiguration.class
)
public class DataLineWsEndpoint { // extends BaseEndpoint {

    public static final Logger log = Logger.getLogger(DataLineWsEndpoint.class.getSimpleName());

    private static final String REPLAYER_KEY = "replayer";
    private static final String RECOGNITION_KEY = "recognition";

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
        Log.info("onOpen");
        log.info(getClass().getSimpleName());
        //sessionService.addSession(session);
        log.info("config.getUserProperties()");
        Principal userPrincipal = (Principal) config.getUserProperties().get("UserPrincipal");
        log.info("userPrincipal");
        log.info(userPrincipal);
        User current = BaseEndpoint.current(userPrincipal, userService);
        log.info("User: current");
        log.info(current);
        // TODO if user not in role stop! (with sufficient error message)

//        Boolean userInRole =  (Boolean) config.getUserProperties().get("userInRole");
//        log.info("userInRole");
//        log.info(userInRole);
    }

    @OnClose
    public void onClose(Session session) {
        Log.info("onClose");
        log.info(getClass().getSimpleName());
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
//    private PlayerActionDecoder pad = new PlayerActionDecoder();

    // https://docs.oracle.com/middleware/12213/wls/WLPRG/websockets.htm#WLPRG1000
    @OnMessage
    public void onMessage(String msg, Session session) {
        Log.info("onDataLineMessage");
        log.info(msg.toString());
        //String loggedUserName = session.getUserPrincipal().getName();
        // TODO add role check and restrict access for users gestures only... etc. etc.

        // TODO input message as Object was not working and inheritance for multiple decoders neither ..
        // 1) it turned out that mvn install was not changing decoders properly for preinstalled non-clean instance of
        // WF, so try that?
        // 2) add decoders as custom bean injections?
        // 3) willDecode(msg) and decode(msg) should work in "extended manner" so no repetitive check and as less of
        // checks and partials conversions would occur .. change if-if_else-else to deal with static/meta data as much
        // as possible with [Finger|Wrist]DataLine extensibility of [Replay|*]Action
        // 3) messages should be possibly simplified - no log fields, etc.
        // 3.1) TODO write (custom) json<->binary (de/en)coders?

        DataLine dl = null;

        if (fdld.willDecode(msg)) {
            MFingerDataLine mfdl = fdld.decode(msg);
            dl = dataLineService.create(mfdl.getEntity(gestureService));
        } else if (wdld.willDecode(msg)) {
            MWristDataLine mwdl = wdld.decode(msg);
            dl = dataLineService.create(mwdl.getEntity(gestureService));
        } else if (dld.willDecode(msg)) {
            MDataLine mdl = dld.decode(msg);
            dl = dataLineService.create(mdl.getEntity(gestureService));
        }

        if (dl != null && rec.isRecognizing()) {
            List<GestureMatcher> lgm = rec.recognize(dl);
            try {
                session.getBasicRemote().sendObject(lgm);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            }
        }

        // considering no DataLine matched...
        if (dl == null && ad.willDecode(msg)) {
            log.info("ad.decode(msg)");
            log.info(msg);
//            Action act = ad.decode(msg);
//            switch(act.getType()) {
//                case RECOGNITION: {
            if (rad.willDecode(msg)) {
                log.info("rad");
                RecognitionActions ra = (RecognitionActions) rad.decode(msg);
                switch (ra.getAction()) {
                    case START: {
                        // stop existing first
//                            Thread recognizer = (Thread) session.getUserProperties().get(RECOGNITION_KEY);
//                            rec.setGestureId(pa.getGestureId());
//                            rec.setSession(session);
//                            if(recognizer == null) {
//                                recognizer = new Thread(rec);
//                            }
                        log.info("RECOGNITION - START, RECOGNITION_KEY: " + RECOGNITION_KEY);

                        Principal userPrincipal = (Principal) session.getUserProperties().get("UserPrincipal");
                        log.info("userPrincipal");
                        log.info(userPrincipal);
                        User current = BaseEndpoint.current(userPrincipal, userService);
                        log.info("current");
                        log.info(current);
                        rec.start(current);
                        session.getUserProperties().put(RECOGNITION_KEY, rec);
                        break;
                    }
                    case STOP: {
                        // TODO fix,
                        // - this is not a Runnable (comparing to Player ..)
                        GestureRecognizer recStored = (GestureRecognizer) session.getUserProperties().get(RECOGNITION_KEY);
//                        synchronized (recStored) {
                            if (recStored != null) {
                                rec.stop();
                            }
//                        }
                        break;
                    }
                }
            } else if (pad.willDecode(msg)) {
                log.info("pad");
//                }
//                case PLAYER: {
                PlayerActions pa = (PlayerActions) pad.decode(msg);
                switch (pa.getAction()) {
                    case PLAY: {
                        // stop existing first
                        log.info("action: " + PlayerActions.ActionsEnum.PLAY);
                        Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
                        rep.prepare(true);
                        rep.setGestureId(pa.getGestureId());
                        rep.setSession(session);
                        if (replayer == null) {
                            replayer = new Thread(rep);
                        }
                        replayer.start();
                        session.getUserProperties().put(REPLAYER_KEY, replayer);
                        break;
                    }
                    case PAUSE: {
                        Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
                        synchronized (replayer) {
                            log.info("Replayer pause");
                            log.info(replayer.toString());
                            if (replayer != null) {
                                rep.pause();
                            }
                        }
                        break;
                    }
                    case CONTINUE: {
                        Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
                        synchronized (replayer) {
                            if (replayer != null) {
                                rep.play();
                                replayer.notify();
                            }
                        }
                        break;
                    }
                    case STOP: {
                        Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
                        synchronized (replayer) {
                            if (replayer != null) {
                                rep.stop();
                            }
                        }
                        break;
                    }
                }
            }
//                }
//                break;
//            }
        } else {
            log.info("Unknown message: " + msg);
        }
    }

    // Exception handling
    @OnError
    public void onError(Session session, Throwable t) {
        Log.info("onError");
        log.info(getClass().getSimpleName());
        t.printStackTrace();
        Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
        if (replayer != null) {
            replayer.interrupt();// stop();
        }
    }

}
