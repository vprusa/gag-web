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

import cz.muni.fi.gag.web.dao.WristDataLineDao;
import cz.muni.fi.gag.web.entity.FingerPosition;
import cz.muni.fi.gag.web.entity.WristDataLine;

import javax.inject.Inject;

import java.util.Date;
import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class WristDataLineTest extends TestBase {

    private static Logger log = Logger.getLogger(WristDataLineTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, WristDataLineTest.class.getSimpleName() + ".war")
                .addPackages(true, "cz.muni.fi.gag.web")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
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
        r.setPosition(FingerPosition.INDEX);
        r.setQuatA(0);
        r.setQuatX(0);
        r.setQuatY(0);
        r.setQuatZ(0);
        r.setTimestamp(new Date());
        r.setX((short) 0);
        r.setY((short) 0);
        r.setZ((short) 0);
        r.setMagX((short) 0);
        r.setMagY((short) 0);
        r.setMagZ((short) 0);

        return r;
    }

    @Before
    public void before() {
        testWristDataLine1 = buildWristDataLine();
        testWristDataLine2 = buildWristDataLine();
        log.info("Using WristDataLine test1:");
        log.info(testWristDataLine1.toString());
        log.info("Using WristDataLine test2:");
        log.info(testWristDataLine2.toString());
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
