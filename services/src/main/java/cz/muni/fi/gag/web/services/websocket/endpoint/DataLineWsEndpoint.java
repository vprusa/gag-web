package cz.muni.fi.gag.web.services.websocket.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.gag.web.persistence.entity.*;
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
import jdk.nashorn.internal.ir.debug.JSONWriter;
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
@ServerEndpoint(value = "/datalinews",
        encoders = {
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


//    private PlayerActionDecoder pad = new PlayerActionDecoder();

    //    private static int limit = 0;
    // https://docs.oracle.com/middleware/12213/wls/WLPRG/websockets.htm#WLPRG1000
    @OnMessage
    public void onMessage(String msg, Session session) {
        Log.info("onDataLineMessage");
        log.info(msg.toString());

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

        if (!rec.isRecognizing()) {
        // This was an ugly way to duplicate real data but it was at hand ... TODO move this to test as automation
//        if (limit <= 12) {
//            limit++;
//            long gid = 79l;
            if (fdld.willDecode(msg)) {
                MFingerDataLine mfdl = fdld.decode(msg);
//                mfdl.setGestureID(gid);
                FingerDataLine fdl = mfdl.getEntity(gestureService);
//                fdl.setGestureID(gid);
                dl = dataLineService.create(fdl);
            } else if (wdld.willDecode(msg)) {
                MWristDataLine mwdl = wdld.decode(msg);
//                mwdl.setGestureID(gid);
                mwdl.setPosition(Sensor.WRIST); // TODO move this somewhere else...
                WristDataLine wdl = mwdl.getEntity(gestureService);
                wdl.setPosition(Sensor.WRIST);
                dl = dataLineService.create(wdl);
            } else if (dld.willDecode(msg)) {
                MDataLine mdl = dld.decode(msg);
//                mdl.setGestureID(gid);
                dl = dataLineService.create(mdl.getEntity(gestureService));
            }
        } else {
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
        Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
        if (replayer != null) {
            replayer.interrupt();// stop();
        }
    }

}
