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

import cz.muni.fi.gag.web.dao.FingerSensorOffsetDao;
import cz.muni.fi.gag.web.entity.FingerSensorOffset;
import cz.muni.fi.gag.web.entity.HandDevice;
import cz.muni.fi.gag.web.entity.SensorType;

import javax.inject.Inject;

import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class FingerSensorOffsetTest extends TestBase {

    private static Logger log = Logger.getLogger(FingerSensorOffsetTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, FingerSensorOffsetTest.class.getSimpleName() + ".war")
                .addPackages(true, "cz.muni.fi.gag.web")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private FingerSensorOffsetDao fignerSensorOffsetDao;

    @Inject
    private FingerSensorOffsetService fingerSensorOffsetService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private FingerSensorOffset testFingerSensorOffset1;
    private FingerSensorOffset testFingerSensorOffset2;

    @Before
    public void before() {
        testFingerSensorOffset1 = buildFingerSensorOffsetWithPersistentRefs();
        testFingerSensorOffset2 = buildFingerSensorOffsetWithPersistentRefs();
        log.info("Using FingerSensorOffset test1:");
        log.info(testFingerSensorOffset1.toString());
        log.info("Using FingerSensorOffset test2:");
        log.info(testFingerSensorOffset2.toString());
    }

    @After
    public void after() {
        fignerSensorOffsetDao.findAll().stream().map(FingerSensorOffset::getId).forEach(fignerSensorOffsetDao::remove);
    }

    @Test
    public void testValidCreateThenFind() {
        testFingerSensorOffset1 = fignerSensorOffsetDao.create(testFingerSensorOffset1);
        // log.info("\n\n\nRandMsg\n\n\n");
        // log.info(testFingerSensorOffset1.toString());
        // log.info(fignerSensorOffsetDao.findAll().toString());
        // log.info(fignerSensorOffsetDao.find(testFingerSensorOffset1.getId()).toString());

        Assert.assertEquals(testFingerSensorOffset1, fignerSensorOffsetDao.find(testFingerSensorOffset1.getId()).get());
    }

    @Test
    public void testServiceValidCreateThenFind() {
        fingerSensorOffsetService.create(testFingerSensorOffset2);
        Assert.assertEquals(testFingerSensorOffset2,
                fingerSensorOffsetService.findById(testFingerSensorOffset2.getId()).get());
    }

    // @Test
    public void testServiceValidUpdateThenFind() {
        testServiceValidCreateThenFind();
        // TODO ...
    }

}
