package cz.muni.fi.gag.web.websocket.endpoint.packet.datalines;

import cz.muni.fi.gag.web.mapped.MWristDataLine;

public class WristDataLineDecoder extends DataLineDecoders<MWristDataLine> {
    public WristDataLineDecoder() {
            super(MWristDataLine.class);
        }
}