package cz.muni.fi.gag.tests.service;

import cz.muni.fi.gag.tests.common.TestServiceBase;
import cz.muni.fi.gag.web.service.GestureService;
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

import cz.muni.fi.gag.web.dao.FingerDataLineDao;
import cz.muni.fi.gag.web.dao.GestureDao;
import cz.muni.fi.gag.web.dao.UserDao;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.entity.User;

import javax.inject.Inject;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import static java.util.Arrays.asList;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class GestureTest extends TestServiceBase {

    private static Logger log = Logger.getLogger(GestureTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(GestureTest.class);
    }
    @Inject
    private GestureDao gestureDao;

    @Inject
    private FingerDataLineDao fignerDataLineDao;

    @Inject
    private UserDao userDao;

    @Inject
    private GestureService gestureService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Gesture testGesture1;
    private Gesture testGesture2;

    public Gesture buildGesture() {
        User u = buildUser();
        u = userDao.create(u);
        FingerDataLine fd = buildFingerDataLine();
        fd = fignerDataLineDao.create(fd);
        List<DataLine> d = Collections.singletonList(fd);

        Gesture r = new Gesture();
        r.setData(d);
        r.setDateCreated(new Date());
        r.setUserAlias("gestureNameAlias");
        r.setUser(u);
        return r;
    }

    @Before
    public void before() {
        testGesture1 = buildGesture();
        testGesture2 = buildGesture();
        printTestEntities(asList(testGesture1, testGesture2));
    }

    @After
    public void after() {
        gestureDao.findAll().stream().map(Gesture::getId).forEach(gestureDao::remove);
    }

    @Test
    public void testValidCreateThenFind() {
        gestureDao.create(testGesture1);
        Assert.assertEquals(testGesture1, gestureDao.find(testGesture1.getId()).get());
    }

    @Test
    public void testServiceValidCreateThenFind() {
        gestureService.create(testGesture2);
        Assert.assertEquals(testGesture2, gestureService.findById(testGesture2.getId()).get());
    }

    // @Test
    public void testServiceValidUpdateThenFind() {
        testServiceValidCreateThenFind();
        // TODO ...
    }

}
