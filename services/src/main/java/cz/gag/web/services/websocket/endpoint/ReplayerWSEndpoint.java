package cz.gag.web.services.websocket.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gag.web.persistence.entity.User;
import cz.gag.web.services.logging.Log;
import cz.gag.web.services.rest.endpoint.BaseEndpoint;
import cz.gag.web.services.service.DataLineService;
import cz.gag.web.services.service.GestureService;
import cz.gag.web.services.service.UserService;
import cz.gag.web.services.websocket.endpoint.config.CustomServerEndpointConfiguration;
import cz.gag.web.services.websocket.endpoint.packet.actions.Action;
import cz.gag.web.services.websocket.endpoint.packet.actions.ActionDecoder;
import cz.gag.web.services.websocket.endpoint.packet.actions.PlayerActions;
import cz.gag.web.services.websocket.endpoint.packet.datalines.DataLineEncoder;
import cz.gag.web.services.websocket.endpoint.packet.datalines.FingerDataLineEncoder;
import cz.gag.web.services.websocket.endpoint.packet.datalines.WristDataLineEncoder;
import cz.gag.web.services.websocket.service.DataLineRePlayer;

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
public class ReplayerWSEndpoint/* extends BaseWSEndpoint*/ {

    public static final Log.TypedLogger log = new Log.TypedLogger<Log.LoggerTypeWSReplayer>(Log.LoggerTypeWSReplayer.class);

    public static final String REPLAYER_KEY = "replayer";

    @Inject
    private DataLineService dataLineService;

    @Inject
    private GestureService gestureService;

    @Inject
    private UserService userService;

    @Inject
    private DataLineRePlayer replayer;

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
    private ActionDecoder<Action> actionDecoder = new ActionDecoder(Action.class);
    private ActionDecoder<PlayerActions> playerActionDecoder = new ActionDecoder(PlayerActions.class, Action.ActionsTypesEnum.PLAYER);

    private ObjectMapper objectMapper = new ObjectMapper();

    // https://docs.oracle.com/middleware/12213/wls/WLPRG/websockets.htm#WLPRG1000
    @OnMessage
    public void onMessage(String msg, Session session) {
        log.info("ReplayerWSEndpoint.onMessage");
        log.info(msg.toString());

        // considering no DataLine matched...
        if (actionDecoder.willDecode(msg)) {
            log.info("ad.decode(msg)");
            log.info(msg);
            if (playerActionDecoder.willDecode(msg)) {
                log.info("pad");
                PlayerActions pa = (PlayerActions) playerActionDecoder.decode(msg);
                switch (pa.getAction()) {
                    case PLAY: {
                        // stop existing first
                        log.info("Action: " + PlayerActions.ActionsEnum.PLAY);
                        Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
                        this.replayer.prepare(true);
                        this.replayer.setGestureId(pa.getGestureId());
                        this.replayer.setSession(session);
                        if (replayer == null) {
                            replayer = new Thread(this.replayer);
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
                                this.replayer.pause();
                            }
                        }
                        break;
                    }
                    case CONTINUE: {
                        Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
                        synchronized (replayer) {
                            if (replayer != null) {
                                this.replayer.play();
                                replayer.notify();
                            }
                        }
                        break;
                    }
                    case STOP: {
                        Thread replayer = (Thread) session.getUserProperties().get(REPLAYER_KEY);
                        synchronized (replayer) {
                            if (replayer != null) {
                                this.replayer.stop();
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
