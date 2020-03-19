package cz.muni.fi.gag.tests.service;

import cz.muni.fi.gag.tests.common.TestServiceBase;
import cz.muni.fi.gag.web.dao.UserDao;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.services.service.UserService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.logging.Logger;

import static java.util.Arrays.asList;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class UserTest extends TestServiceBase {

    private static Logger log = Logger.getLogger(UserTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(UserTest.class);
    }

    @Inject
    private UserDao userDao;

    @Inject
    private UserService userService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private User testUser1;
    private User testUser2;

    @Before
    public void before() {
        testUser1 = buildUser();
        testUser2 = buildUser();
        printTestEntities(asList(testUser1, testUser2));
    }

    @After
    public void after() {
        userDao.findAll().stream().map(User::getId).forEach(userDao::remove);
    }

    @Test
    public void testValidCreateThenFind() {
        userDao.create(testUser1);
        Assert.assertEquals(testUser1, userDao.find(testUser1.getId()).get());
    }

    @Test
    public void testServiceValidCreateThenFind() {
        userService.create(testUser2);
        Assert.assertEquals(testUser2, userService.findById(testUser2.getId()).get());
    }

    // @Test
    public void testServiceValidUpdateThenFind() {
        testServiceValidCreateThenFind();
        // TODO ...
    }

}
