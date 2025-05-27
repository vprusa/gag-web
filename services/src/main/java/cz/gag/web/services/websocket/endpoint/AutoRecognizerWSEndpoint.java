package cz.gag.web.services.websocket.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gag.web.persistence.entity.DataLine;
import cz.gag.web.persistence.entity.User;
import cz.gag.web.services.logging.Log;
import cz.gag.web.services.recognition.matchers.MultiSensorGestureMatcher;
import cz.gag.web.services.rest.endpoint.BaseEndpoint;
import cz.gag.web.services.service.DataLineService;
import cz.gag.web.services.service.GestureService;
import cz.gag.web.services.service.UserService;
import cz.gag.web.services.websocket.endpoint.config.CustomServerEndpointConfiguration;
import cz.gag.web.services.websocket.service.GestureRecognizer;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@ServerEndpoint(
        value = "/ws/autoRecognizer",
        configurator = CustomServerEndpointConfiguration.class
)
public class AutoRecognizerWSEndpoint {

    private static final Log.TypedLogger log = new Log.TypedLogger<>(Log.LoggerTypeWSRecognizer.class);

    @Inject
    private GestureRecognizer recognizer;

    @Inject
    private UserService userService;

    @Inject
    private GestureService gestureService;

    @Inject
    private DataLineService dataLineService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        log.info("AutoRecognizerWSEndpoint.onOpen");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            // Parse JSON input with two gesture IDs
            RecognitionRequest req = objectMapper.readValue(message, RecognitionRequest.class);
            long gestureIdToRecognize = req.getTargetGestureId();
            long gestureIdToReplay = req.getGestureToReplayId();

            // Get user from session context
            Principal principal = (Principal) session.getUserProperties().get("UserPrincipal");
            User currentUser = BaseEndpoint.current(principal, userService);

            // Start recognition with the gesture(s) activated for this user
            recognizer.start(currentUser);

            // Load and stream sensor data for the gesture to be replayed
            List<DataLine> replayData = dataLineService.findByGestureId(gestureIdToReplay);

            for (DataLine dl : replayData) {
                List<MultiSensorGestureMatcher> results = recognizer.recognize(dl);
                if (!results.isEmpty()) {
                    session.getBasicRemote().sendText(objectMapper.writeValueAsString(results));
                }
            }

            // Optionally send ACK or summary
            session.getBasicRemote().sendText("{\"status\": \"done\"}");
            recognizer.stop();

        } catch (Exception e) {
            log.info("AutoRecognizerWSEndpoint.onError");
            e.printStackTrace();
            try {
                session.getBasicRemote().sendText("{\"error\": \"" + e.getMessage() + "\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            recognizer.stop();
        }
    }

    @OnClose
    public void onClose(Session session) {
        recognizer.stop();
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
        recognizer.stop();
    }

    // Helper class to deserialize JSON input
    public static class RecognitionRequest {
        private long targetGestureId;
        private long gestureToReplayId;

        public long getTargetGestureId() {
            return targetGestureId;
        }

        public void setTargetGestureId(long targetGestureId) {
            this.targetGestureId = targetGestureId;
        }

        public long getGestureToReplayId() {
            return gestureToReplayId;
        }

        public void setGestureToReplayId(long gestureToReplayId) {
            this.gestureToReplayId = gestureToReplayId;
        }
    }
}
