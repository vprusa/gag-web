package cz.gag.web.services.websocket.endpoint.packet.datalines;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gag.web.persistence.entity.DataLine;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import org.jboss.logging.Logger;

/**
 * @author Vojtech Prusa
 */
public class DataLineEncoders<DataLineEx extends DataLine> implements Encoder.Text<DataLineEx> {

    public static final Logger log = Logger.getLogger(DataLineEncoders.class.getSimpleName());

    private ObjectMapper objectMapper;

    public DataLineEncoders() {
        objectMapper = new ObjectMapper();
    }

    @Override
    public String encode(DataLineEx object) throws EncodeException {
        try {
            String str = objectMapper.writeValueAsString(object);
            log.info(str);
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
    public void destroy() {
        // TODO https://www.geeksforgeeks.org/how-to-make-object-eligible-for-garbage-collection/
        //objectMapper = null;
    }
}
