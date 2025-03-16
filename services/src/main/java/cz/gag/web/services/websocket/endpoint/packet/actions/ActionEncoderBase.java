package cz.gag.web.services.websocket.endpoint.packet.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author Vojtech Prusa
 *
 * Note: No extended classes needed here 'encode(Act a)' method does it all..
 */
public class ActionEncoderBase<Act extends Action> implements Encoder.Text<Act> {

    public static final Logger log = Logger.getLogger(ActionEncoderBase.class.getSimpleName());

    private ObjectMapper objectMapper;

    Class type;

    public ActionEncoderBase(Class type) {
        this.type = type;
        objectMapper = new ObjectMapper();
    }

    @Override
    public String encode(Act a) {
//        log.info("decode: " + this.getClass().getSimpleName());
        String str = null;
        try {
            str = objectMapper.writeValueAsString(a);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return str;
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
