package cz.gag.web.services.websocket.endpoint.packet.recognition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gag.web.services.recognition.matchers.MultiSensorGestureMatcher;
import org.jboss.logging.Logger;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author Vojtech Prusa
 */
public class RecognizedGestureEncoder implements Encoder.Text<MultiSensorGestureMatcher> {

    public static final Logger log = Logger.getLogger(RecognizedGestureEncoder.class.getSimpleName());

    private ObjectMapper objectMapper;

    public RecognizedGestureEncoder() {
        objectMapper = new ObjectMapper();
    }

    @Override
    public String encode(MultiSensorGestureMatcher object) throws EncodeException {
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
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
