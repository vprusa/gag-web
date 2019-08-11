package cz.muni.fi.gag.web.websocket.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.WristDataLine;
import org.jboss.logging.Logger;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author Vojtech Prusa
 */
public class DataLineEncoders<DataLineEx extends DataLine> implements Encoder.Text<DataLineEx> {

    public static final Logger log = Logger.getLogger(DataLineWsEndpoint.class.getSimpleName());

    public static class Plain extends DataLineEncoders<DataLine> {}
    public static class Finger extends DataLineEncoders<FingerDataLine> {}
    public static class Wrist extends DataLineEncoders<WristDataLine> {}

    private ObjectMapper objectMapper;

    @Override
    public String encode(DataLineEx object) throws EncodeException {
        try {
            String str = objectMapper.writeValueAsString(object);
            DataLineEncoders.log.info(str);
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
