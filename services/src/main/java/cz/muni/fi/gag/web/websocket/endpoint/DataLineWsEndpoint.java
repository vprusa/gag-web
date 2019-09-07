package cz.muni.fi.gag.web.websocket.endpoint;

import cz.muni.fi.gag.web.logging.Log;
import cz.muni.fi.gag.web.service.DataLineService;
import cz.muni.fi.gag.web.service.GestureService;
import cz.muni.fi.gag.web.service.UserService;
import cz.muni.fi.gag.web.websocket.endpoint.packet.actions.Action;
import cz.muni.fi.gag.web.websocket.endpoint.packet.actions.ActionDecoder;
import cz.muni.fi.gag.web.websocket.endpoint.packet.actions.ReplayActionDecoder;
import cz.muni.fi.gag.web.websocket.endpoint.packet.datalines.DataLineEncoder;
import cz.muni.fi.gag.web.websocket.endpoint.packet.datalines.FingerDataLineEncoder;
import cz.muni.fi.gag.web.websocket.endpoint.packet.datalines.WristDataLineEncoder;
import javax.ejb.Singleton;
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
@Singleton
@ServerEndpoint(value = "/datalinews",
        encoders = { DataLineEncoder.class, FingerDataLineEncoder.class, WristDataLineEncoder.class},
        decoders = { ReplayActionDecoder.class, ActionDecoder.class,
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
    
    //@Inject
    //private DataLineRePlayer rep;

    @OnOpen
    public void onOpen(Session session) {
        Log.info("onOpen");
        log.info(getClass().getSimpleName());
        //sessionService.addSession(session);
        // sendPushUpdate(dataLineService.getTopTenLastDay(), session);
    }

    @OnClose
    public void onClose(Session session) {
        // LogMessages.LOGGER.logWebsocketDisconnect(getClass().getSimpleName());
        Log.info("onClose");
        log.info(getClass().getSimpleName());
        Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
        if(replayer != null) {
            replayer.interrupt();// stop();
        }
    }


    // https://docs.oracle.com/middleware/12213/wls/WLPRG/websockets.htm#WLPRG1000
    @OnMessage
    public void onMessage(Action msg, Session session) {
        Log.info("onDataLineMessage");
        String loggedUserName = session.getUserPrincipal().getName();
        // TODO add role check and restrict access for users gestures only... etc. etc.

        /*
        // TODO wrap in some structure .. create WS control protocol ...
        if(msg instanceof MWristDataLine){
            MWristDataLine wdl  = (MWristDataLine) msg;
            dataLineService.create(wdl);
        } else if(msg instanceof MFingerDataLine) {
            MFingerDataLine fdl  = (MFingerDataLine) msg;
            dataLineService.create(fdl);
        } else if(msg instanceof MDataLine) {
            MDataLine dl  = (MDataLine) msg;
            dataLineService.create(dl);

         */
        if(false) {

        /*
        } else if (msg instanceof Action) {
            Action act = (Action) msg;
            log.info(act.toString());
         */
        // else if (msg instanceof String) {
           /*
            String msgStr = (String) msg;
            JsonReader jsonReader = Json.createReader(new StringReader(msgStr));
            JsonObject object = jsonReader.readObject();

            String action = object.getString("action");
            log.info("Action: " + action);

            switch(action){
                case "replayGesture":
                    // {\"action\" : \"replayGesture\", \"gestureId\":2}";
                    long gestureId = object.getJsonNumber("gestureId").longValue();
                    jsonReader.close();
                    log.info("gestureId: " + gestureId);

                    //List<DataLine> gestureData = dataLineService.findByGestureId(gestureId);
                    // if(isUserInRole(loggedUserName, Role.ADMIN) || isUserInRole(loggedUserName,
                    // Role.SUPER_USER)) {
                    // dataLineService.recommend(songId, loggedUserName);
                    // }
                    DataLineRePlayer rep = new DataLineRePlayer(session, dataLineService, gestureId);
                    Thread replayer = new Thread(rep);
                    replayer.start();
                    session.getUserProperties().put(REPLAYER_KEY, replayer);
                    break;
                default:
                    log.info("Unknown action: " + action);
                    return;


            }
            */
        } else {
            log.info("Unknown message: " + msg.toString());
        }

    }
    
    // Exception handling
    @OnError
    public void onError(Session session, Throwable t) {
        Log.info("onError");
        log.info(getClass().getSimpleName());
        t.printStackTrace();
    }

}
