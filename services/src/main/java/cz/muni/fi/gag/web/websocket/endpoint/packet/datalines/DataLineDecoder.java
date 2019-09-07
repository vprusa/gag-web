package cz.muni.fi.gag.web.websocket.endpoint.packet.datalines;

import cz.muni.fi.gag.web.mapped.MDataLine;

public class DataLineDecoder extends DataLineDecoders<MDataLine> {
    public DataLineDecoder() {
            super(MDataLine.class);
        }
}