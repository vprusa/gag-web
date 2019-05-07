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

import cz.muni.fi.gag.web.dao.UserDao;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.web.entity.UserRole;

import javax.inject.Inject;

import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 */
 @RunWith(Arquillian.class)
public class UserTest extends TestBase {

    private static Logger log = Logger.getLogger(UserTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
          return ShrinkWrap.create(WebArchive.class, UserTest.class.getSimpleName() + ".war")
                .addPackages(true, "cz.muni.fi.gag.web")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
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
        log.info("Using User test1:");
        log.info(testUser1.toString());
        log.info("Using User test2:");
        log.info(testUser2.toString());
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

    //@Test
    public void testServiceValidUpdateThenFind() {
      testServiceValidCreateThenFind();
      // TODO ...
    }

}
