package cz.muni.fi.gag.web.websocket.endpoint;

import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.logging.Log;
import cz.muni.fi.gag.web.service.DataLineService;
import cz.muni.fi.gag.web.service.GestureService;
import cz.muni.fi.gag.web.service.UserService;
import cz.muni.fi.gag.web.websocket.service.DataLineMessage;
import cz.muni.fi.gag.web.websocket.service.DataLineRePlayer;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.List;

/**
 * @author Vojtech Prusa
 */
@Singleton
@ServerEndpoint(value = "/datalinews",
        //encoders = Plain.class
        encoders = { DataLineCoders.Plain.class, DataLineCoders.Finger.class, DataLineCoders.Wrist.class},
        decoders = { DataLineDecoders.Plain.class, DataLineDecoders.Finger.class, DataLineDecoders.Wrist.class}
        //encoders = {DataLineCoders.Plain.Serializer.class,
        //DataLineCoders.Finger.Serializer.class, DataLineCoders.Wrist.Serializer.class}
//, decoders = {DataLineCoders.Deserializer.class}
)
//@ServerEndpoint("/datalinews")
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

    // It starts a Thread that notifies all sessions each second
    @PostConstruct
    public void startIntervalNotifier() {
        Log.info("startIntervalNotifier");
        Log.info(getClass().getSimpleName());
        log.info(getClass().getSimpleName());
    }

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
        if(replayer != null)
            replayer.interrupt();// stop();
        // replayer.destroy();
        //sessionService.removeSession(session);
    }

    @OnMessage
    public void onDataLineMessage(String msg, Session session) {
        Log.info("onDataLineMessage");
        log.info(getClass().getSimpleName());
        String loggedUserName = session.getUserPrincipal().getName();
        // TODO add role check and restrict access for users gestures only... etc. etc.
        
        log.info("gestureId: " + msg);
        long gestureId = 2;
        
        List<DataLine> gestureData = dataLineService.findByGestureId(gestureId);
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

    public void onReplay(@Observes @DataLineMessage List<DataLine.Aggregate> dataline) {
        //sendPushUpdate(dataline);
    }

    private void sendPushUpdate(List<DataLine.Aggregate> dataline) {
        //sessionService.getAllSessions().stream().forEach(session -> {
        //    session.getAsyncRemote().sendObject(dataline);
        //});
    }

    private void sendPushUpdate(List<DataLine.Aggregate> dataline, Session session) {
        //session.getAsyncRemote().sendObject(dataline);
    }

    // private boolean isUserInRole(String loggedUserName, UserRole admin) {
    // return
    // userService.findByIdentificator(loggedUserName).getRole().equals(admin);
    // }

}
