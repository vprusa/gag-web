package cz.muni.fi.gag.web.websocket.endpoint.packet.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import org.jboss.logging.Logger;

/**
 * @author Vojtech Prusa
 */
public abstract class ActionDecoderAbstr<Act extends Action> implements Decoder.Text<Act> {

    public static final Logger log = Logger.getLogger(ActionDecoderAbstr.class.getSimpleName());

    private ObjectMapper objectMapper;

    Class type;

    public ActionDecoderAbstr(Class type) {
        this.type = type;
        objectMapper = new ObjectMapper();
    }

    @Override
    public Act decode(String s) {
        ObjectReader objectReader = objectMapper.reader().forType(type);
        try {
            Act cmd = objectReader.readValue(s);
            log.info(cmd.toString());
            return cmd;
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
