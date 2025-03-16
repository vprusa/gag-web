package cz.gag.web.services.websocket.endpoint.packet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import javax.json.JsonObject;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author Vojtech Prusa
 */
public class JsonEncoder implements Encoder.Text<JsonObject> {

    public static final Logger log = Logger.getLogger(JsonEncoder.class.getSimpleName());

    private ObjectMapper objectMapper;

    @Override
    public String encode(JsonObject object) throws EncodeException {
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
