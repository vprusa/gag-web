package cz.gag.tests.common;

import cz.gag.web.services.service.UserService;
import org.jboss.logging.Logger;

import javax.inject.Inject;

/**
 * @author Vojtech Prusa
 */
public class TestEndpointBase extends TestServiceBase {

    private static Logger log = Logger.getLogger(TestEndpointBase.class.getSimpleName());

    @Inject
    private UserService userService;

}
