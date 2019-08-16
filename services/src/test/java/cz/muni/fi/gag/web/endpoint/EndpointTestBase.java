package cz.muni.fi.gag.web.endpoint;

import cz.muni.fi.gag.web.entity.AbstractEntity;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 * TODO refactor to endpoints ...
 *
 * {@link FingerDataLineEndpointTest}
 */
@RunWith(Arquillian.class)
public abstract class EndpointTestBase<EntityExt extends AbstractEntity>
        extends AuthenticationTestBase {

    private static Logger log = Logger.getLogger(EndpointTestBase.class.getSimpleName());
    protected static final String API_ENDPOINT = AuthenticationTestBase.APP_URL+"api/";

}
