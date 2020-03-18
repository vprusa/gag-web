package cz.muni.fi.gag.tests.endpoint;

import cz.muni.fi.gag.web.entity.GenericEntity;
import org.apache.http.HttpResponse;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
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
public abstract class EndpointTestBase<EntityExt extends GenericEntity>
        extends AuthenticationTestBase {

    private static Logger log = Logger.getLogger(EndpointTestBase.class.getSimpleName());
    protected static final String API_ENDPOINT = AuthenticationTestBase.APP_URL+"api/";

    protected StringBuilder entityContentToString(HttpResponse response) {
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder;
    }
}
