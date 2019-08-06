package cz.muni.fi.gag.web.service;

import cz.muni.fi.gag.web.common.TestServiceBase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import cz.muni.fi.gag.web.dao.FingerDataLineDao;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.FingerPosition;

import java.util.Date;
import java.util.logging.Logger;

import javax.inject.Inject;
import static java.util.Arrays.asList;

/**
 * @author Vojtech Prusa
 *
 */
//@ArquillianSuiteDeployment
@RunWith(Arquillian.class)
public class FingerDataLineTest extends TestServiceBase {

    private static Logger log = Logger.getLogger(FingerDataLineTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(FingerDataLineTest.class);
    }
    
    @Inject
    public FingerDataLineDao fignerDataLineDao;

    @Inject
    public FingerDataLineService fingerDataLineService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private FingerDataLine testFingerDataLine1;
    private FingerDataLine testFingerDataLine2;

    public FingerDataLine buildFingerDataLine() {
        FingerDataLine r = new FingerDataLine();
        r.setGesture(null);
        r.setPosition(FingerPosition.INDEX);
        r.setQuatA(0);
        r.setQuatX(0);
        r.setQuatY(0);
        r.setQuatZ(0);
        r.setTimestamp(new Date());
        r.setAccX((short) 0);
        r.setAccY((short) 0);
        r.setAccZ((short) 0);
        return r;
    }

    @Before
    public void before() {
        testFingerDataLine1 = buildFingerDataLine();
        testFingerDataLine2 = buildFingerDataLine();
        printTestEntities(asList(testFingerDataLine1, testFingerDataLine2));
    }

    @After
    public void after() {
        fignerDataLineDao.findAll().stream().map(FingerDataLine::getId).forEach(fignerDataLineDao::remove);
    }

    @Test
    public void testValidCreateThenFind() {
        fignerDataLineDao.create(testFingerDataLine1);
        Assert.assertEquals(testFingerDataLine1, fignerDataLineDao.find(testFingerDataLine1.getId()).get());
    }

    @Test
    public void testServiceValidCreateThenFind() {
        fingerDataLineService.create(testFingerDataLine2);
        // log.info(testFingerDataLine2.toString());
        // log.info(fingerDataLineService.findById(testFingerDataLine2.getId()).get().toString());
        // log.info(testFingerDataLine2.hashCode() + "");
        // log.info(fingerDataLineService.findById((testFingerDataLine2.getId()).get().hashCode()
        // + "");
        // log.info(testFingerDataLine2.equals(fingerDataLineService.findById(testFingerDataLine2.getId()).get())
        // + "");
        Assert.assertEquals(testFingerDataLine2, fingerDataLineService.findById(testFingerDataLine2.getId()).get());
        after();
    }

}
