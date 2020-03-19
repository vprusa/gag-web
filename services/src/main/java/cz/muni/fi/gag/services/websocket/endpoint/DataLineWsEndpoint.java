package cz.muni.fi.gag.services.websocket.endpoint;

import cz.muni.fi.gag.services.logging.Log;
import cz.muni.fi.gag.services.mapped.MDataLine;
import cz.muni.fi.gag.services.mapped.MFingerDataLine;
import cz.muni.fi.gag.services.mapped.MWristDataLine;
import cz.muni.fi.gag.services.service.DataLineService;
import cz.muni.fi.gag.services.service.GestureService;
import cz.muni.fi.gag.services.service.UserService;
import cz.muni.fi.gag.services.websocket.endpoint.packet.actions.PlayerActionDecoder;
import cz.muni.fi.gag.services.websocket.endpoint.packet.actions.PlayerActions;
import cz.muni.fi.gag.services.websocket.endpoint.packet.datalines.*;
import cz.muni.fi.gag.services.websocket.service.DataLineRePlayer;
import org.jboss.logging.Logger;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * @author Vojtech Prusa
 */
@SessionScoped
@ServerEndpoint(value = "/datalinews",
        encoders = {
                //JsonEncoder.class
                DataLineEncoder.class, FingerDataLineEncoder.class, WristDataLineEncoder.class
                 },
        decoders = {}
)
public class DataLineWsEndpoint {

    public static final Logger log = Logger.getLogger(DataLineWsEndpoint.class.getSimpleName());

    private static final String REPLAYER_KEY = "replayer";

    @Inject
    private DataLineService dataLineService;

    @Inject
    private GestureService gestureService;

    @Inject
    private UserService userService;

    @Inject
    private DataLineRePlayer rep;

    @OnOpen
    public void onOpen(Session session) {
        Log.info("onOpen");
        log.info(getClass().getSimpleName());
        //sessionService.addSession(session);
    }

    @OnClose
    public void onClose(Session session) {
        Log.info("onClose");
        log.info(getClass().getSimpleName());
        Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
        if(replayer != null) {
            replayer.interrupt();// stop();
        }
    }

    // TODO add decoders as custom bean injections?
    private DataLineDecoder dld = new DataLineDecoder();
    private FingerDataLineDecoder fdld = new FingerDataLineDecoder();
    private WristDataLineDecoder wdld = new WristDataLineDecoder();
    private PlayerActionDecoder pad = new PlayerActionDecoder();

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

        if(fdld.willDecode(msg)) {
            MFingerDataLine mfdl = fdld.decode(msg);
            dataLineService.create(mfdl.getEntity(gestureService));
        } else if(wdld.willDecode(msg)) {
            MWristDataLine mwdl = wdld.decode(msg);
            dataLineService.create(mwdl.getEntity(gestureService));
        } else if(dld.willDecode(msg)) {
            MDataLine mdl = dld.decode(msg);
            dataLineService.create(mdl.getEntity(gestureService));
        } else if(pad.willDecode(msg)) {
            PlayerActions pa = pad.decode(msg);
            switch(pa.getType()){
                case PLAYER: {
                    switch (pa.getAction()) {
                        case PLAY: {
                            // stop existing first
                            Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
                            rep.prepare(true);
                            rep.setGestureId(pa.getGestureId());
                            rep.setSession(session);
                            replayer = new Thread(rep);
                            replayer.start();
                            session.getUserProperties().put(REPLAYER_KEY, replayer);
                        }
                        break;
                        case PAUSE: {
                            Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
                            synchronized (replayer) {
                                log.info("Replayer pause");
                                log.info(replayer.toString());
                                if (replayer != null) {
                                    rep.pause();
                                }
                            }
                        }
                        break;
                        case CONTINUE: {
                            Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
                            synchronized (replayer) {
                                if (replayer != null) {
                                    rep.play();
                                    replayer.notify();
                                }
                            }
                        }
                        break;
                        case STOP: {
                            Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
                            synchronized (replayer) {
                                if (replayer != null) {
                                    rep.stop();
                                }
                            }

                        }
                        break;
                    }
                }
                break;
            }
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
        if(replayer != null) {
            replayer.interrupt();// stop();
        }
    }

}
