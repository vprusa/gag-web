package cz.gag.web.services.websocket.endpoint.packet.datalines;

import cz.gag.web.services.mapped.MFingerDataLine;

public class FingerDataLineDecoder extends DataLineDecoders<MFingerDataLine> {
    public FingerDataLineDecoder() {
            super(MFingerDataLine.class);
        }
    @Override
    public boolean willDecode(String s) {
        log.info("FingerDataLineDecoder - willDecode: " + s);
        // TODO check position 
//        return s.contains("\"qX\"") && !s.contains("\"mX\"") && !s.contains("\"p:\"WRIST\"\"");
//        if(s.contains("{id=")) {
            // if Hash<String,String>
//            return s.contains("qX") && !s.contains("mX") && !s.contains("p=WRIST");
//        }
        return s.contains("\"qX\"") && !s.contains("\"mX\"") && !s.contains("\"p:\"WRIST\"\"");
    }
}