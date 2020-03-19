package cz.muni.fi.gag.services.websocket.endpoint.packet;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import org.jboss.logging.Logger;

/**
 * @author Vojtech Prusa
 */
public class JsonDecoder2 implements Decoder.Text<JsonObject> {

    public static final Logger log = Logger.getLogger(JsonDecoder2.class.getSimpleName());

    private ObjectMapper objectMapper;

    @Override
    public JsonObject decode(String s) {
        //ObjectReader objectReader = objectMapper.reader();//.forType(JsonObject.class);
        JsonReader reader = Json.createReader(new StringReader(s));
        JsonStructure jsonst = reader.read();
        JsonObject obj = (JsonObject) jsonst;
        //JsonObject obj = new JsonObject(s); //objectReader.readVareadValue(s);
        log.info(obj.toString());
        return obj;
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
