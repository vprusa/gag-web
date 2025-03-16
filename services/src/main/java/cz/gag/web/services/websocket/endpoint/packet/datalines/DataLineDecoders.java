package cz.gag.web.services.websocket.endpoint.packet.datalines;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import cz.gag.web.persistence.entity.DataLine;
import cz.gag.web.services.logging.Log;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

/**
 * @author Vojtech Prusa
 */
public class DataLineDecoders<DataLineEx extends DataLine> implements Decoder.Text<DataLineEx> {

//    public static final Logger log = Logger.getLogger(DataLineDecoders.class.getSimpleName());
    public static final Log.TypedLogger log = new Log.TypedLogger<Log.LoggerTypeDLDecoder>(Log.LoggerTypeDLDecoder.class);

    private final Class type;

    public DataLineDecoders(Class type) {
        this.type = type;
        objectMapper = new ObjectMapper();
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
        log.info("TODO ... willDecode");
        log.info(this.getClass().getSimpleName());
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
