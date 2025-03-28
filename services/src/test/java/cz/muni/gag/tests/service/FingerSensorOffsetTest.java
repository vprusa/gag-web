package cz.gag.tests.service;

import cz.gag.tests.common.TestServiceBase;
import cz.gag.web.persistence.entity.Sensor;
import cz.gag.web.services.service.FingerSensorOffsetService;
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

import cz.gag.web.persistence.dao.FingerSensorOffsetDao;
import cz.gag.web.persistence.entity.FingerSensorOffset;

import javax.inject.Inject;

import java.util.logging.Logger;
import static java.util.Arrays.asList;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class FingerSensorOffsetTest extends TestServiceBase {

    private static Logger log = Logger.getLogger(FingerSensorOffsetTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(FingerSensorOffsetTest.class);
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
        testFingerSensorOffset1 = buildFingerSensorOffsetWithPersistentRefs(Sensor.INDEX);
        testFingerSensorOffset2 = buildFingerSensorOffsetWithPersistentRefs(Sensor.INDEX);
        printTestEntities(asList(testFingerSensorOffset1, testFingerSensorOffset2));
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
