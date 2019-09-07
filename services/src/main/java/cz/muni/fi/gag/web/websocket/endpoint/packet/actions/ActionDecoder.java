package cz.muni.fi.gag.web.websocket.endpoint.packet.actions;

/**
 * @author Vojtech Prusa
 */
public class ActionDecoder extends ActionDecoderAbstr<Action> {

    public ActionDecoder() {
        super(Action.class);
    }

    @Override
    public boolean willDecode(String s) {
        log.info("ActionDecoder - willDecode: " + s);
        return !s.contains("gestureId");
    }
}