package cz.gag.tests.service;

import cz.gag.tests.common.TestServiceBase;
import cz.gag.web.services.service.WristDataLineService;
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

import cz.gag.web.persistence.dao.WristDataLineDao;
import cz.gag.web.persistence.entity.Sensor;
import cz.gag.web.persistence.entity.WristDataLine;

import javax.inject.Inject;

import java.util.Date;
import java.util.logging.Logger;

import static java.util.Arrays.asList;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class WristDataLineTest extends TestServiceBase {

    private static Logger log = Logger.getLogger(WristDataLineTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(WristDataLineTest.class);
    }
    
    @Inject
    private WristDataLineDao wristDataLineDao;

    @Inject
    private WristDataLineService wristDataLineService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private WristDataLine testWristDataLine1;
    private WristDataLine testWristDataLine2;

    public WristDataLine buildWristDataLine() {
        WristDataLine r = new WristDataLine();

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
        r.setMagX((short) 0);
        r.setMagY((short) 0);
        r.setMagZ((short) 0);

        return r;
    }

    @Before
    public void before() {
        testWristDataLine1 = buildWristDataLine();
        testWristDataLine2 = buildWristDataLine();
        printTestEntities(asList(testWristDataLine1, testWristDataLine2));
    }

    @After
    public void after() {
        wristDataLineDao.findAll().stream().map(WristDataLine::getId).forEach(wristDataLineDao::remove);
    }

    @Test
    public void testValidCreateThenFind() {
        wristDataLineDao.create(testWristDataLine1);
        Assert.assertEquals(testWristDataLine1, wristDataLineDao.find(testWristDataLine1.getId()).get());
    }

    @Test
    public void testServiceValidCreateThenFind() {
        wristDataLineService.create(testWristDataLine2);
        Assert.assertEquals(testWristDataLine2, wristDataLineService.findById(testWristDataLine2.getId()).get());
    }

    // @Test
    public void testServiceValidUpdateThenFind() {
        testServiceValidCreateThenFind();
        // TODO ...
    }

}
