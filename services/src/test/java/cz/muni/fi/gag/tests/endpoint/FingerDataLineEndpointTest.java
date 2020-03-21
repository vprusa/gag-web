package cz.muni.fi.gag.tests.endpoint;

import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Sensor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 * TODO refactor to endpoints ...
 *
 */
@RunWith(Arquillian.class)
public class FingerDataLineEndpointTest extends EndpointTestBase<FingerDataLine> {

    private static Logger log = Logger.getLogger(FingerDataLineEndpointTest.class.getSimpleName());

    public static final String TESTED_ENDPOINT = API_ENDPOINT + "fingerdataline";

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(FingerDataLineEndpointTest.class);
    }

    public FingerDataLine buildDataLine() {
        FingerDataLine r = new FingerDataLine();
        r.setGesture(null);
        r.setPosition(Sensor.INDEX);
        r.setQuatA(0);
        r.setQuatX(0);
        r.setQuatY(0);
        r.setQuatZ(0);
        r.setTimestamp(new Date());
        r.setAccX((short) 0);
        r.setAccY((short) 0);
        r.setAccZ((short) 0);
        return r;
    }

    @Test
    @RunAsClient
    public void testEndpoint() throws Exception {
        HttpClient client = new DefaultHttpClient();//ClientBuilder.newClient();
        String accessToken = basicLogin();

        HttpPost insertPost = new HttpPost(TESTED_ENDPOINT);
        insertPost.addHeader("Authorization", "Bearer " + accessToken);
        //StringEntity params =new StringEntity(
        //        "{\"id\":5,\"timestamp\":-3599005,\"quatA\":1.0,\"quatX\":1.0,\"quatY\":1.0,\"quatZ\":1.0,"+
        //                "\"position\":\"THUMB\",\"magX\":1,\"magY\":1,\"magZ\":1,\"x\":1,\"y\":1,\"z\":1}");
        //StringEntity params =new StringEntity(
        //                "{\"timestamp\":-3599005,\"quatA\":1.0,\"quatX\":1.0,\"quatY\":1.0,\"quatZ\":1.0,"+
        //                         "\"position\":\"THUMB\",\"magX\":1,\"magY\":1,\"magZ\":1,\"x\":1,\"y\":1,\"z\":1}");
        StringEntity params =new StringEntity(
                "{\"timestamp\":-3599005,\"quatA\":1.0,\"quatX\":1.0,\"quatY\":1.0,\"quatZ\":1.0,"+
                        "\"position\":\"THUMB\",\"x\":1,\"y\":1,\"z\":1}");
        insertPost.setEntity(params);
        insertPost.setHeader("Content-Type", "application/json");

        HttpResponse response = client.execute(insertPost);
        log.info(response.toString());
        log.info(String.valueOf(response.getStatusLine().getStatusCode()));

        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        String text = stringBuilder.toString();
        log.info(text);
    }

}
