package cz.muni.fi.gag.web.endpoint;

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
    public void withTokenShouldSayHello() throws Exception {
        HttpClient client = new DefaultHttpClient();//ClientBuilder.newClient();
        HttpGet get = new HttpGet(getAppUrl());
        String accessToken = basicLogin();
        get.addHeader("Authorization", "Bearer " + accessToken);
        HttpResponse response = client.execute(get);
        assertEquals("Status code should have been 200", 200, response.getStatusLine().getStatusCode());
    }

}
