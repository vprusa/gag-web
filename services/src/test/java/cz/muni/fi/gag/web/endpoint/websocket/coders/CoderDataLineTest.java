package cz.muni.fi.gag.web.endpoint.websocket.coders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.gag.web.common.TestServiceBase;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.WristDataLine;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.websocket.EncodeException;

import static org.junit.Assert.assertEquals;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class CoderDataLineTest extends TestServiceBase {

    private static final Logger log = Logger.getLogger(CoderDataLineTest.class.getSimpleName());

    public static class DataLineCoderTester<DataLineEx extends DataLine> {
        public String testEncoder(DataLineEx dl) throws EncodeException, JsonProcessingException {
            DataLineEncoders<DataLineEx> decoder = new DataLineEncoders<DataLineEx>();
            decoder.init(null);
            String encoded = decoder.encode(dl);
            log.info(encoded);
            // TODO assert here
            String expected = jsonEncode(dl);
            assertEquals("Encoded string does not equals: ", expected, encoded);
            return encoded;
        }

        public final ObjectMapper objectMapper = new ObjectMapper();

        public String jsonEncode(DataLineEx dl) throws JsonProcessingException {
            String str = objectMapper.writeValueAsString(dl);
            log.info(str);
            return str;
        }

        public DataLineEx testDecoder(DataLineEx dl) throws EncodeException, JsonProcessingException {
            String s = testEncoder(dl);
            // have to se id because default value is null due to handling id's automatically via persistence layer
            dl.setId(0);

            DataLineDecoders<DataLineEx> decoder = new DataLineDecoders<DataLineEx>(dl.getClass());
            decoder.init(null);
            DataLineEx decoded = decoder.decode(s);
            log.info(decoded);
            assertEquals("Decoded DataLine does not equals:", dl, decoded);
            return decoded;
        }
    }

    @Test
    @RunAsClient
    public void testDataLineEncoder() throws EncodeException, JsonProcessingException {
        new DataLineCoderTester<DataLine>().testEncoder(buildDataLine());
    }

    @Test
    @RunAsClient
    public void testDataLineDecoder() throws EncodeException, JsonProcessingException {
        new DataLineCoderTester<DataLine>().testDecoder(buildDataLine());
    }

    @Test
    @RunAsClient
    public void testFingerDataLineDecoder() throws EncodeException, JsonProcessingException {
        new DataLineCoderTester<FingerDataLine>().testDecoder(buildFingerDataLine());
    }

    @Test
    @RunAsClient
    public void testFingerDataLineEncoder() throws EncodeException, JsonProcessingException {
        new DataLineCoderTester<FingerDataLine>().testEncoder(buildFingerDataLine());
    }

    @Test
    @RunAsClient
    public void testWristDataLineEncoder() throws EncodeException, JsonProcessingException {
        new DataLineCoderTester<WristDataLine>().testEncoder(buildWristDataLine());
    }

    @Test
    @RunAsClient
    public void testWristDataLineDecoder() throws EncodeException, JsonProcessingException {
        new DataLineCoderTester<WristDataLine>().testDecoder(buildWristDataLine());
    }

}
