package cz.muni.fi.gag.web.websocket.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.WristDataLine;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.util.List;

/**
 * @author Vojtech Prusa
 *
 */
public abstract class DataLineCoders<DataLineExU extends DataLine> {

    //public class AbPlain extends DataLineCoders<DataLine> {}
    public class Plain extends DataLineCoders<DataLine> {}
    public class Finger extends DataLineCoders<FingerDataLine> {}
    public class Wrist extends DataLineCoders<WristDataLine> {}

    public abstract class Serializer2<DataLineEx extends DataLineExU> implements Encoder.Text<List<DataLine.Aggregate<DataLineEx>>> {

        private ObjectMapper objectMapper;

        @Override
        public String encode(List<DataLine.Aggregate<DataLineEx>> object) throws EncodeException {
            try {
                return objectMapper.writeValueAsString(object);
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

    public class DataLineSerializer implements Encoder.Text<List<DataLine.Aggregate<DataLine>>> {

        private ObjectMapper objectMapper;

        @Override
        public String encode(List<DataLine.Aggregate<DataLine>> object) throws EncodeException {
            try {
                return objectMapper.writeValueAsString(object);
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

/*
    public class Deserializer implements Decoder.Text<List<DataLine.Aggregate>> {
        private ObjectMapper objectMapper;

        @Override
        public String encode(List<DataLine.Aggregate> object) throws EncodeException {
            try {
                return objectMapper.writeValueAsString(object);
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

        }

        @Override
        public List<DataLine.Aggregate> decode(String s) throws DecodeException {
            try {
                return objectMapper.writeValueAsString(s);
            } catch (JsonProcessingException e) {
                throw new EncodeException(object, e.getMessage(), e);
            };
        }

        @Override
        public boolean willDecode(String s) {
            return false;
        }
    }
*/
}
