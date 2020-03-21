package cz.muni.fi.gag.tests.service;

import cz.muni.fi.gag.tests.common.TestServiceBase;
import cz.muni.fi.gag.web.services.service.HandDeviceService;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import cz.muni.fi.gag.web.persistence.dao.FingerSensorOffsetDao;
import cz.muni.fi.gag.web.persistence.dao.HandDeviceDao;
import cz.muni.fi.gag.web.persistence.dao.UserDao;
import cz.muni.fi.gag.web.persistence.entity.HandDevice;

import javax.inject.Inject;

import java.util.logging.Logger;
import static java.util.Arrays.asList;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class HandDeviceTest extends TestServiceBase {

    private static Logger log = Logger.getLogger(HandDeviceTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(HandDevice.class);
    }

    @Inject
    private HandDeviceDao handDeviceDao;

    @Inject
    private FingerSensorOffsetDao fingerSensorOffsetDao;

    @Inject
    private HandDeviceService handDeviceService;

    @Inject
    private UserDao userDao;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private HandDevice testHandDevice1;
    private HandDevice testHandDevice2;

    @Before
    public void before() {
        testHandDevice1 = buildHandDevice();
        testHandDevice2 = buildHandDevice();
        printTestEntities(asList(testHandDevice1, testHandDevice2));
    }

    @After
    public void after() {
        handDeviceDao.findAll().stream().map(HandDevice::getId).forEach(handDeviceDao::remove);
    }

    @Test
    public void testValidCreateThenFind() {
        handDeviceDao.create(testHandDevice1);
        Assert.assertEquals(testHandDevice1, handDeviceDao.find(testHandDevice1.getId()).get());
    }

    @Test
    public void testServiceValidCreateThenFind() {
        handDeviceService.create(testHandDevice2);
        Assert.assertEquals(testHandDevice2, handDeviceService.findById(testHandDevice2.getId()).get());
    }

    // @Test
    public void testServiceValidUpdateThenFind() {
        testServiceValidCreateThenFind();
        // TODO ...
    }

}
