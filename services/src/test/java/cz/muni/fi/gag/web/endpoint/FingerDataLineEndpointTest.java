package cz.muni.fi.gag.web.endpoint;

import cz.muni.fi.gag.web.dao.FingerDataLineDao;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.FingerPosition;
import cz.muni.fi.gag.web.service.FingerDataLineService;
import cz.muni.fi.gag.web.common.TestEndpointBase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Date;
import java.util.logging.Logger;

import static java.util.Arrays.asList;

/**
 * @author Vojtech Prusa
 *
 * TODO refactor to endpoints ...
 *
 */
@RunWith(Arquillian.class)
public class FingerDataLineEndpointTest extends TestEndpointBase {

    private static Logger log = Logger.getLogger(FingerDataLineEndpointTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(FingerDataLineEndpointTest.class);
    }
    
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
        r.setX((short) 0);
        r.setY((short) 0);
        r.setZ((short) 0);
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
        fingerDataLineService.findAll().stream().forEach(fingerDataLineService::remove);
    }

    @Test
    public void testEndpointCRUD() {
        fingerDataLineService.create(testFingerDataLine2);
        Assert.assertEquals(testFingerDataLine2, fingerDataLineService.findById(testFingerDataLine2.getId()).get());
        //after();
    }

}
