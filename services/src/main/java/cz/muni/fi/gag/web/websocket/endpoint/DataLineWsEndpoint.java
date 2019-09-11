package cz.muni.fi.gag.web.websocket.endpoint;

import cz.muni.fi.gag.web.logging.Log;
import cz.muni.fi.gag.web.mapped.MDataLine;
import cz.muni.fi.gag.web.mapped.MFingerDataLine;
import cz.muni.fi.gag.web.mapped.MWristDataLine;
import cz.muni.fi.gag.web.service.DataLineService;
import cz.muni.fi.gag.web.service.GestureService;
import cz.muni.fi.gag.web.service.UserService;
import cz.muni.fi.gag.web.websocket.endpoint.packet.actions.ReplayAction;
import cz.muni.fi.gag.web.websocket.endpoint.packet.actions.ReplayActionDecoder;
import cz.muni.fi.gag.web.websocket.endpoint.packet.datalines.DataLineDecoder;
import cz.muni.fi.gag.web.websocket.endpoint.packet.datalines.DataLineEncoder;
import cz.muni.fi.gag.web.websocket.endpoint.packet.datalines.FingerDataLineDecoder;
import cz.muni.fi.gag.web.websocket.endpoint.packet.datalines.FingerDataLineEncoder;
import cz.muni.fi.gag.web.websocket.endpoint.packet.datalines.WristDataLineDecoder;
import cz.muni.fi.gag.web.websocket.endpoint.packet.datalines.WristDataLineEncoder;
import cz.muni.fi.gag.web.websocket.service.DataLineRePlayer;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.jboss.logging.Logger;

/**
 * @author Vojtech Prusa
 */
//@Singleton
@SessionScoped
@ServerEndpoint(value = "/datalinews",
        encoders = {
                //JsonEncoder.class
                DataLineEncoder.class, FingerDataLineEncoder.class, WristDataLineEncoder.class
                 },
        decoders = {
            //JsonDecoder2.class
            //ReplayActionDecoder.class, ActionDecoder.class,
            /*
            DataLineDecoder.class, FingerDataLineDecoder.class, WristDataLineDecoder.class
            */}

)
public class DataLineWsEndpoint {

    public static final Logger log = Logger.getLogger(DataLineWsEndpoint.class.getSimpleName());

    private static final String REPLAYER_KEY = "replayer";

    //@Inject
    //private SessionService sessionService;

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
    //private static ActionDecoder ad = new ActionDecoder();
    private ReplayActionDecoder rad = new ReplayActionDecoder();

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
        } else if(rad.willDecode(msg)) {
            ReplayAction ra = rad.decode(msg);
            //DataLineRePlayer rep = new DataLineRePlayer(session, dataLineService, ra.getGestureId());
            //rep = new DataLineRePlayer(session, dataLineService, ra.getGestureId());
            //DataLineRePlayer rep = new DataLineRePlayer(session, ra.getGestureId());
            if(rep.getSession() == null) {
                rep.setGestureId(ra.getGestureId());
                rep.setSession(session);
                Thread replayer = new Thread(rep);
                replayer.start();
                session.getUserProperties().put(REPLAYER_KEY, replayer);
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
