package cz.muni.fi.gag.web.services.websocket.endpoint.packet;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

/**
 * Tests, sketches... seriously... do not take this seriously.
 * */
public class Packet {

    // TODO simplify mapping ...
    // necessities:
    // - as static as possible ... so it would be fast
    // - as general as possible ... so changing it would be easy

    public enum Action {
        NONE,
        REAPLAY_GESTURE
    }

    public static class Actions {
        public static class Keys {
            public static final String key = "action";
        }
        public static class Values {
            public static class ReplayGesture {
                public static final String name = "replayeGestue";
                //public static final Action value = Action.REAPLAY_GESTURE;
                public static class GestureId {
                    public static final String name = "gestureId";
                }
            }
        }
    }

    private final String message;
    private final Action action;
    private final long gestureId;

    public Packet(String message) {
        this.message = message;
        JsonReader reader = Json.createReader(new StringReader(message));
        JsonObject jsonMsg = reader.readObject();

        String actionStr = jsonMsg.getString(Actions.Keys.key);
        switch(actionStr){
            case Actions.Values.ReplayGesture.name:
                //action = Actions.Values.ReplayGesture.value;
                action = Action.REAPLAY_GESTURE;
                gestureId = jsonMsg.getJsonNumber(Actions.Values.ReplayGesture.GestureId.name).longValue();
                break;
            default:
                action = Action.NONE;
                gestureId = -1;
                break;
        }
    }

    public String getMessage() {
        return message;
    }

    public Action getAction(){
        return action;
    }

    public long getGestureId(){
        return gestureId;
    }

}
