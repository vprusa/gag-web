package cz.muni.fi.gag.web.websocket.endpoint.packet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.WristDataLine;
import cz.muni.fi.gag.web.websocket.endpoint.DataLineWsEndpoint;
import org.jboss.logging.Logger;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

/**
 * @author Vojtech Prusa
 */
public class DataLineDecoders<DataLineEx extends DataLine> implements Decoder.Text<DataLineEx> {

    public static final Logger log = Logger.getLogger(DataLineWsEndpoint.class.getSimpleName());

    private final Class type;

    public DataLineDecoders(Class type) {
        this.type = type;
    }

    public static class Plain extends DataLineDecoders<DataLine> {
        public Plain() {
            super(DataLine.class);
        }
    }
    public static class Finger extends DataLineDecoders<FingerDataLine> {
        public Finger() {
            super(FingerDataLine.class);
        }
    }
    public static class Wrist extends DataLineDecoders<WristDataLine> {
        public Wrist() {
            super(WristDataLine.class);
        }
    }

    private ObjectMapper objectMapper;

    @Override
    public DataLineEx decode(String s) {
        ObjectReader objectReader = objectMapper.reader().forType(type);
        try {
            DataLineEx dl = objectReader.readValue(s);
            log.info(dl.toString());
            return dl;
        } catch (IOException e) {
            e.printStackTrace();
            //throw new DecodeException("","");
        }
        return null;
    }

    @Override
    public boolean willDecode(String s) {
        // TODO
        log.info("TODO ... willDecode");
        return true;
    }

    @Override
    public void init(EndpointConfig config) {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void destroy() {
        // TODO https://www.geeksforgeeks.org/how-to-make-object-eligible-for-garbage-collection/
        //objectMapper = null;
    }
}
