package cz.muni.fi.gag.web.websocket.endpoint.packet.actions;

/**
 * @author Vojtech Prusa
 */
public class ReplayActionDecoder extends ActionDecoderBase<ReplayAction> {

    public ReplayActionDecoder() {
        super(ReplayAction.class);
    }

    @Override
    public boolean willDecode(String s) {
        log.info("ReplayActionDecoder - willDecode: " + s);
        return s.contains("\"action\"") && s.contains("\"replay\"");
    }
}