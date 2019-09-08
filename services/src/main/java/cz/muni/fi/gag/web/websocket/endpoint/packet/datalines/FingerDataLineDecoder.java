package cz.muni.fi.gag.web.websocket.endpoint.packet.datalines;

import cz.muni.fi.gag.web.mapped.MFingerDataLine;

public class FingerDataLineDecoder extends DataLineDecoders<MFingerDataLine> {
    public FingerDataLineDecoder() {
            super(MFingerDataLine.class);
        }
    @Override
    public boolean willDecode(String s) {
        log.info("FingerDataLineDecoder - willDecode: " + s);
        return s.contains("\"qX\"") && !s.contains("\"mX\"");
    }

}