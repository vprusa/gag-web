package cz.muni.fi.gag.tests.endpoint;

import cz.muni.fi.gag.web.entity.Gesture;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class GestureEndpointTest extends EndpointTestBase<Gesture> {

    private static Logger log = Logger.getLogger(GestureEndpointTest.class.getSimpleName());

    public static final String TESTED_ENDPOINT = API_ENDPOINT + "gesture/filter/21/newGesture" + new Random().nextInt();

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(GestureEndpointTest.class);
    }

    @Test
    @RunAsClient
    public void testGestureFilterEndpoint() throws Exception {
        HttpClient client = new DefaultHttpClient();
        String accessToken = basicLogin();

        HttpPut request = new HttpPut(TESTED_ENDPOINT);
        request.addHeader("Authorization", "Bearer " + accessToken);
        StringEntity params = new StringEntity("");
        request.setEntity(params);
        request.setHeader("Content-Type", "application/json");

        HttpResponse response = client.execute(request);
        log.info(response.toString());
        log.info(String.valueOf(response.getStatusLine().getStatusCode()));

        StringBuilder sb = entityContentToString(response);
        String text = (sb == null ? "BufferedReader is null" : sb.toString());
        log.info(text);
    }

}
