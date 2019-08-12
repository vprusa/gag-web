package cz.muni.fi.gag.web.websocket.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.WristDataLine;
import org.codehaus.jackson.type.TypeReference;
import org.jboss.logging.Logger;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

/**
 * @author Vojtech Prusa
 */
public class DataLineDecoders<DataLineEx extends DataLine> implements Decoder.Text<DataLineEx> {

    public static final Logger log = Logger.getLogger(DataLineWsEndpoint.class.getSimpleName());

    public static class Plain extends DataLineDecoders<DataLine> {}
    public static class Finger extends DataLineDecoders<FingerDataLine> {}
    public static class Wrist extends DataLineDecoders<WristDataLine> {}

    private ObjectMapper objectMapper;
    //private ObjectReader objectReader;

    private class DataLineExType extends TypeReference<DataLineEx> {}

    //private final DataLineExType type = new DataLineExType();

    @Override
    public DataLineEx decode(String s) throws DecodeException {
        // TODO
        log.info("TODO");
        //ObjectReader or = new ObjectReader();
        //ObjectReader objectReader = objectMapper.readerFor(DataLine.class);
        ObjectReader objectReader = objectMapper.reader().forType(DataLineExType.class);
        //(new DataLineExType().getClass()); // Type.class // type.getClass()
        try {
            DataLineEx dl = objectReader.readValue(s);
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
        log.info("TODO");
        return false;
    }

    @Override
    public void init(EndpointConfig config) {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void destroy() {}
}
