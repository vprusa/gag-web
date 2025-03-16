package cz.gag.web.services.websocket.endpoint.packet.recognition;

import cz.gag.web.services.recognition.matchers.MultiSensorGestureMatcher;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

class RecognizedGestureDecoder implements Decoder.Text<MultiSensorGestureMatcher> {

    @Override
    public MultiSensorGestureMatcher decode(String s) throws DecodeException {
        return null;
    }

    @Override
    public boolean willDecode(String s) {
        return s.contains("\"t\"") && !s.contains("\"qX\"");
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}