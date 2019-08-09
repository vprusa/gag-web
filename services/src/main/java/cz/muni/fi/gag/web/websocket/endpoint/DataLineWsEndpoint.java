package cz.muni.fi.gag.web.websocket.endpoint;

import cz.muni.fi.gag.web.logging.Log;
import cz.muni.fi.gag.web.service.DataLineService;
import cz.muni.fi.gag.web.service.GestureService;
import cz.muni.fi.gag.web.service.UserService;
import cz.muni.fi.gag.web.websocket.service.DataLineRePlayer;
import org.jboss.logging.Logger;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.StringReader;

/**
 * @author Vojtech Prusa
 */
@Singleton
@ServerEndpoint(value = "/datalinews",
        encoders = { DataLineCoders.Plain.class, DataLineCoders.Finger.class, DataLineCoders.Wrist.class},
        decoders = { DataLineDecoders.Plain.class, DataLineDecoders.Finger.class, DataLineDecoders.Wrist.class}
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

    @OnMessage
    public void onDataLineMessage(String msg, Session session) {
        Log.info("onDataLineMessage");
        String loggedUserName = session.getUserPrincipal().getName();
        // TODO add role check and restrict access for users gestures only... etc. etc.

        // TODO wrap in some structure .. create WS control protocol ...
        JsonReader jsonReader = Json.createReader(new StringReader(msg));
        JsonObject object = jsonReader.readObject();

        String action = object.getString("action");
        log.info("Action: " + action);
        if(!action.matches("replayGesture")){
            // another TODO ...
            log.info("Unknown action: " + action);
            return;
        }
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
    }
    
    // Exception handling
    @OnError
    public void onError(Session session, Throwable t) {
        Log.info("onError");
        log.info(getClass().getSimpleName());
        t.printStackTrace();
    }

}
