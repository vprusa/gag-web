package cz.muni.fi.gag.web.services.websocket.endpoint.packet.datalines;

import cz.muni.fi.gag.web.services.mapped.MWristDataLine;

public class WristDataLineDecoder extends DataLineDecoders<MWristDataLine> {
    public WristDataLineDecoder() {
            super(MWristDataLine.class);
        }
    @Override
    public boolean willDecode(String s) {
        log.info("WristDataLineDecoder - willDecode: " + s);
        return s.contains("\"qX\"") && s.contains("\"mX\"");
    }
}