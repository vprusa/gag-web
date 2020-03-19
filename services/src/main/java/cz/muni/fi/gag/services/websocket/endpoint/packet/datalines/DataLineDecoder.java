package cz.muni.fi.gag.services.websocket.endpoint.packet.datalines;

import cz.muni.fi.gag.services.mapped.MDataLine;

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