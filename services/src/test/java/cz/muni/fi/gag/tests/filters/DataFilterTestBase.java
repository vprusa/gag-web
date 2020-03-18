package cz.muni.fi.gag.tests.filters;

import cz.muni.fi.gag.tests.common.TestBase;
import cz.muni.fi.gag.tests.endpoint.AuthenticationTestBase;
import cz.muni.fi.gag.tests.endpoint.FingerDataLineEndpointTest;
import cz.muni.fi.gag.web.entity.GenericEntity;
import cz.muni.fi.gag.web.entity.Gesture;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 * TODO refactor to endpoints ...
 *
 * {@link FingerDataLineEndpointTest}
 */
@RunWith(Arquillian.class)
public abstract class DataFilterTestBase<EntityExt extends GenericEntity>
        extends AuthenticationTestBase {

    private static Logger log = Logger.getLogger(DataFilterTestBase.class.getSimpleName());
    protected static final String API_ENDPOINT = AuthenticationTestBase.APP_URL+"api/";

    public static final String TESTED_ENDPOINT = API_ENDPOINT + "fingerdataline";

    @Deployment
    public static WebArchive deployment() {
        return TestBase.getDeployment(FingerDataLineEndpointTest.class);
    }

    public Gesture buildGesture() {
        Gesture r = new Gesture();
//        r.setGesture(null);
        return r;
    }

//    @Test
//    @RunAsClient
    public void testEndpoint() throws Exception {
        HttpClient client = new DefaultHttpClient();//ClientBuilder.newClient();
        String accessToken = basicLogin();

        HttpPost insertPost = new HttpPost(TESTED_ENDPOINT);
        insertPost.addHeader("Authorization", "Bearer " + accessToken);
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
