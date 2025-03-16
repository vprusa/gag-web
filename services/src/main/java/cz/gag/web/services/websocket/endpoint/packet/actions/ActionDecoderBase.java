package cz.gag.web.services.websocket.endpoint.packet.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.jboss.logging.Logger;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

/**
 * @author Vojtech Prusa
 * <p>
 * {@link ActionDecoder}
 * {@link RecognitionActions}
 * {@link PlayerActions}
 */
public abstract class ActionDecoderBase<Act extends Action> implements Decoder.Text<Act> {

    public static final Logger log = Logger.getLogger(ActionDecoderBase.class.getSimpleName());

    private ObjectMapper objectMapper;

    private final Action.ActionsTypesEnum typeEnum;
    private final Class actionClass;

    public ActionDecoderBase(final Class actionClass, final Action.ActionsTypesEnum typeEnum) {
        this.typeEnum = typeEnum;
        this.actionClass = actionClass;
        objectMapper = new ObjectMapper();
    }

    @Override
    public Act decode(String s) {
//        log.info("decode: " + this.getClass().getSimpleName());
        ObjectReader objectReader = objectMapper.reader().forType(actionClass);
        try {
            Act cmd = objectReader.readValue(s);
//            log.info(cmd.toString());
            return cmd;
        } catch (IOException e) {
            e.printStackTrace();
            //throw new DecodeException("","");
        }
        return null;
    }

    @Override
    public boolean willDecode(String s) {
//        log.info(s);
//        log.info("typeEnum");
//        log.info(typeEnum);
//        log.info("actionClass");
//        log.info(actionClass);
        if (typeEnum == null) {
//            log.info("Will ret? " + s.contains("\"type\""));
            return s.contains("\"type\"");
        } else {
//            log.info("Will ret? " + (s.contains("\"type\"") && s.contains("\"" + typeEnum.toString() + "\"")));
//            return s.contains("\"type\"") && s.contains("\"" + actionClass.toString() + "\"");
            return s.contains("\"type\":" + typeEnum.ordinal()) || s.contains("\"type\":\"" + typeEnum.toString() + "\"");
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
