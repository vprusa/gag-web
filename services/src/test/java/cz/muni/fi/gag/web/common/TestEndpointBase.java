package cz.muni.fi.gag.web.common;

import cz.muni.fi.gag.web.service.UserService;
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
