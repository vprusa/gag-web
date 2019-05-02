package cz.muni.fi.gag.web.service;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import cz.muni.fi.gag.web.dao.WristSensorOffsetDao;
import cz.muni.fi.gag.web.entity.HandDevice;
import cz.muni.fi.gag.web.entity.SensorType;
import cz.muni.fi.gag.web.entity.WristSensorOffset;

import javax.inject.Inject;

import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class WristSensorOffsetTest extends TestBase {

    private static Logger log = Logger.getLogger(WristSensorOffsetTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, WristSensorOffsetTest.class.getSimpleName() + ".war")
                .addPackages(true, "cz.muni.fi.gag.web")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private WristSensorOffsetDao wristSensorOffsetDao;

    @Inject
    private WristSensorOffsetService wristSensorOffsetService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private WristSensorOffset testWristSensorOffset1;
    private WristSensorOffset testWristSensorOffset2;

    @Before
    public void before() {
        testWristSensorOffset1 = buildWristSensorOffsetWithPersistentRefs();
        testWristSensorOffset2 = buildWristSensorOffsetWithPersistentRefs();
        log.info("Using WristSensorOffset test1:");
        log.info(testWristSensorOffset1.toString());
        log.info("Using WristSensorOffset test2:");
        log.info(testWristSensorOffset2.toString());
    }

    @After
    public void after() {
        wristSensorOffsetDao.findAll().stream().map(WristSensorOffset::getId).forEach(wristSensorOffsetDao::remove);
    }

    @Test
    public void testValidCreateThenFind() {
        testWristSensorOffset1 = wristSensorOffsetDao.create(testWristSensorOffset1);
        Assert.assertEquals(testWristSensorOffset1, wristSensorOffsetDao.find(testWristSensorOffset1.getId()).get());
    }

    @Test
    public void testServiceValidCreateThenFind() {
        testWristSensorOffset2 = wristSensorOffsetService.create(testWristSensorOffset2);
        Assert.assertEquals(testWristSensorOffset2,
                wristSensorOffsetService.findById(testWristSensorOffset2.getId()).get());
    }

    // @Test
    public void testServiceValidUpdateThenFind() {
        testServiceValidCreateThenFind();
        // TODO ...
    }

}
