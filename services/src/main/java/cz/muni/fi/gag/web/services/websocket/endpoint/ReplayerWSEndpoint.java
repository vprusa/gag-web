package cz.muni.fi.gag.web.services.websocket.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.gag.web.persistence.entity.User;
import cz.muni.fi.gag.web.services.logging.Log;
import cz.muni.fi.gag.web.services.rest.endpoint.BaseEndpoint;
import cz.muni.fi.gag.web.services.service.DataLineService;
import cz.muni.fi.gag.web.services.service.GestureService;
import cz.muni.fi.gag.web.services.service.UserService;
import cz.muni.fi.gag.web.services.websocket.endpoint.config.CustomServerEndpointConfiguration;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.Action;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.ActionDecoder;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.PlayerActions;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.datalines.DataLineEncoder;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.datalines.FingerDataLineEncoder;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.datalines.WristDataLineEncoder;
import cz.muni.fi.gag.web.services.websocket.service.DataLineRePlayer;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.security.Principal;

/**
 * @author Vojtech Prusa
 */
@SessionScoped
@ServerEndpoint(value = "/ws/replayer",
        encoders = {
                DataLineEncoder.class, FingerDataLineEncoder.class, WristDataLineEncoder.class
        },
        decoders = {},
        configurator = CustomServerEndpointConfiguration.class
)
public class ReplayerWSEndpoint extends BaseWSEndpoint {

    public static final Log.TypedLogger log = new Log.TypedLogger<Log.LoggerTypeWSReplayer>(Log.LoggerTypeWSReplayer.class);

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
    private ActionDecoder<Action> ad = new ActionDecoder(Action.class);
    private ActionDecoder<PlayerActions> pad = new ActionDecoder(PlayerActions.class, Action.ActionsTypesEnum.PLAYER);

    private ObjectMapper objectMapper = new ObjectMapper();

    // https://docs.oracle.com/middleware/12213/wls/WLPRG/websockets.htm#WLPRG1000
    @OnMessage
    public void onMessage(String msg, Session session) {
        log.info("ReplayerWSEndpoint.onMessage");
        log.info(msg.toString());

        // considering no DataLine matched...
        if (ad.willDecode(msg)) {
            log.info("ad.decode(msg)");
            log.info(msg);
            if (pad.willDecode(msg)) {
                log.info("pad");
                PlayerActions pa = (PlayerActions) pad.decode(msg);
                switch (pa.getAction()) {
                    case PLAY: {
                        // stop existing first
                        log.info("Action: " + PlayerActions.ActionsEnum.PLAY);
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
