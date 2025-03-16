package cz.gag.tests.endpoint;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class AuthenticationTest extends AuthenticationTestBase {

    private static final Logger log = Logger.getLogger(AuthenticationTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(AuthenticationTest.class);
    }

    @Test
    public void withTokenShouldRespond() throws IOException, UnsupportedEncodingException {
        HttpClient client = new DefaultHttpClient();//ClientBuilder.newClient();
        HttpGet get = new HttpGet(getAppUrl());
        String accessToken = basicLogin();
        get.addHeader("Authorization", "Bearer " + accessToken);
        HttpResponse response = client.execute(get);
        /*try {
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }*/
        log.info(response.getEntity());
        log.info(response.getStatusLine());
        assertEquals("Status code should have been 200", 200, response.getStatusLine().getStatusCode());
    }

}
