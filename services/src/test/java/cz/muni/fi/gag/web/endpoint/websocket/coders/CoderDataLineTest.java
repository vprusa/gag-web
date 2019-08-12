package cz.muni.fi.gag.web.endpoint.websocket.coders;

import cz.muni.fi.gag.web.common.TestServiceBase;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.WristDataLine;
import cz.muni.fi.gag.web.websocket.endpoint.DataLineDecoders;
import cz.muni.fi.gag.web.websocket.endpoint.DataLineEncoders;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.websocket.DecodeException;
import javax.websocket.EncodeException;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class CoderDataLineTest extends TestServiceBase {

    private static final Logger log = Logger.getLogger(CoderDataLineTest.class.getSimpleName());

    public static class DataLineCoderTester<DataLineEx extends DataLine> {
        public String testEncoder(DataLineEx dl) throws EncodeException {
            DataLineEncoders<DataLineEx> decoder = new DataLineEncoders<DataLineEx>();
            decoder.init(null);
            String encoded = decoder.encode(dl);
            log.info(encoded);
            // TODO assert here
            return encoded;
        }

        public DataLineEx testDecoder(DataLineEx dl) throws DecodeException, EncodeException {
            String s = testEncoder(dl);
            DataLineDecoders<DataLineEx> decoder = new DataLineDecoders<DataLineEx>();
            decoder.init(null);
            DataLineEx decoded = decoder.decode(s);
            log.info(decoded);
            // TODO assert here
            return decoded;
        }
    }

    @Test
    @RunAsClient
    public void testDataLineEncoder() throws EncodeException {
        log.info("testDataLineEncoder");
        new DataLineCoderTester<DataLine>().testEncoder(buildDataLine());
    }

    @Test
    @RunAsClient
    public void testFingerDataLineEncoder() throws EncodeException {
        log.info("testFingerDataLineEncoder");
        new DataLineCoderTester<FingerDataLine>().testEncoder(buildFingerDataLine());
    }

    @Test
    @RunAsClient
    public void testWristDataLineEncoder() throws EncodeException {
        log.info("testWristDataLineEncoder");
        new DataLineCoderTester<WristDataLine>().testEncoder(buildWristDataLine());
    }

    @Test
    @RunAsClient
    public void testDataLineDecoder() throws EncodeException, DecodeException {
        log.info("testDataLineDecoder");
        new DataLineCoderTester<DataLine>().testDecoder(buildDataLine());
    }

    @Test
    @RunAsClient
    public void testFingerDataLineDecoder() throws EncodeException, DecodeException {
        log.info("testFingerDataLineDecoder");
        new DataLineCoderTester<FingerDataLine>().testDecoder(buildFingerDataLine());
    }

    @Test
    @RunAsClient
    public void testWristDataLineDecoder() throws EncodeException, DecodeException {
        log.info("testWristDataLineDecoder");
        new DataLineCoderTester<WristDataLine>().testDecoder(buildWristDataLine());
    }

}
