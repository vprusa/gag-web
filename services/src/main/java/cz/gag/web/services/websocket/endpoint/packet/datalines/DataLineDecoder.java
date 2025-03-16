package cz.gag.web.services.websocket.endpoint.packet.datalines;

import cz.gag.web.services.mapped.MDataLine;

public class DataLineDecoder extends DataLineDecoders<MDataLine> {
    public DataLineDecoder() {
        super(MDataLine.class);
    }
    @Override
    public boolean willDecode(String s) {
        log.info("DataLineDecoder - willDecode: " + s);
        return s.contains("\"t\"") && !s.contains("\"qX\"");
    }
}