package cz.muni.fi.gag.web.services.websocket.endpoint;

import cz.muni.fi.gag.web.persistence.entity.*;
import cz.muni.fi.gag.web.services.logging.Log;
import cz.muni.fi.gag.web.services.mapped.MDataLine;
import cz.muni.fi.gag.web.services.mapped.MFingerDataLine;
import cz.muni.fi.gag.web.services.mapped.MWristDataLine;
import cz.muni.fi.gag.web.services.rest.endpoint.BaseEndpoint;
import cz.muni.fi.gag.web.services.service.DataLineService;
import cz.muni.fi.gag.web.services.service.GestureService;
import cz.muni.fi.gag.web.services.service.UserService;
import cz.muni.fi.gag.web.services.websocket.endpoint.config.CustomServerEndpointConfiguration;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.datalines.*;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.security.Principal;

/**
 * @author Vojtech Prusa
 */
@SessionScoped
@ServerEndpoint(value = "/ws/recorder",
        encoders = {
                DataLineEncoder.class, FingerDataLineEncoder.class, WristDataLineEncoder.class
        },
        decoders = {},
        configurator = CustomServerEndpointConfiguration.class
)
public class RecorderWSEndpoint  extends BaseWSEndpoint {

    public static final Log.TypedLogger log = new Log.TypedLogger<Log.LoggerTypeWSRecorder>(Log.LoggerTypeWSRecorder.class);

    private static final String REPLAYER_KEY = "replayer";

    @Inject
    private DataLineService dataLineService;

    @Inject
    private GestureService gestureService;

    @Inject
    private UserService userService;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
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

    @OnMessage
    public void onMessage(String msg, Session session) {
        log.info("RecorderWSEndpoint.onDataLineMessage");
        log.info(msg.toString());
        DataLine dl = null;

        if (fdld.willDecode(msg)) {
            MFingerDataLine mfdl = fdld.decode(msg);
            FingerDataLine fdl = mfdl.getEntity(gestureService);
            dl = dataLineService.create(fdl);
        } else if (wdld.willDecode(msg)) {
            MWristDataLine mwdl = wdld.decode(msg);
            mwdl.setPosition(Sensor.WRIST); // TODO move this somewhere else...
            WristDataLine wdl = mwdl.getEntity(gestureService);
            wdl.setPosition(Sensor.WRIST);
            dl = dataLineService.create(wdl);
        } else if (dld.willDecode(msg)) {
            MDataLine mdl = dld.decode(msg);
            dl = dataLineService.create(mdl.getEntity(gestureService));
        }
    }

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
