package cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions;

/**
 * @author Vojtech Prusa
 */
public class PlayerActionDecoder extends ActionDecoderBase<PlayerActions> {

    public PlayerActionDecoder() {
        super(PlayerActions.class);
    }

    @Override
    public boolean willDecode(String s) {
        log.info("PlayerActionDecoder - willDecode: " + s);
        return s.contains("\"type\"");
    }
}