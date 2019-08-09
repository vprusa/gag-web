package cz.muni.fi.gag.web.websocket.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.WristDataLine;
import org.jboss.logging.Logger;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;

/**
 * @author Vojtech Prusa
 */
public class DataLineDecoders<DataLineEx extends DataLine> implements Decoder.Text<DataLineEx> {

    public static final Logger log = Logger.getLogger(DataLineWsEndpoint.class.getSimpleName());

    public static class Plain extends DataLineDecoders<DataLine> {}
    public static class Finger extends DataLineDecoders<FingerDataLine> {}
    public static class Wrist extends DataLineDecoders<WristDataLine> {}

    private ObjectMapper objectMapper;

    @Override
    public DataLineEx decode(String s) throws DecodeException {
        // TODO
        return null;
    }

    @Override
    public boolean willDecode(String s) {
        // TODO
        return false;
    }

    //@Override
    public String encode(DataLineEx object) throws EncodeException {
        try {
            String str = objectMapper.writeValueAsString(object);;
            DataLineDecoders.log.info(str);
            return str;
        } catch (JsonProcessingException e) {
            throw new EncodeException(object, e.getMessage(), e);
        }
    }

    @Override
    public void init(EndpointConfig config) {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void destroy() {}
}
