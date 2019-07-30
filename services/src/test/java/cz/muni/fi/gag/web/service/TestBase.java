package cz.muni.fi.gag.web.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.jboss.logging.Logger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import cz.muni.fi.gag.web.dao.FingerSensorOffsetDao;
import cz.muni.fi.gag.web.dao.HandDeviceDao;
import cz.muni.fi.gag.web.dao.UserDao;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.FingerPosition;
import cz.muni.fi.gag.web.entity.FingerSensorOffset;
import cz.muni.fi.gag.web.entity.HandDevice;
import cz.muni.fi.gag.web.entity.SensorOffset;
import cz.muni.fi.gag.web.entity.SensorType;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.web.entity.UserRole;
import cz.muni.fi.gag.web.entity.WristSensorOffset;

/**
 * @author Vojtech Prusa
 */
public class TestBase {

    private static Logger log = Logger.getLogger(TestBase.class.getSimpleName());

    static String keycloakGroupId = "org.keycloak:";
    static String keycloakVersion = ":6.0.1";

    //@Deployment
    public static WebArchive getDeployment(Class clazz) {
       /*
        File[] files = Maven.resolver()
                .resolve(keycloakGroupId + "keycloak-core" + keycloakVersion,
                        keycloakGroupId + "keycloak-common" + keycloakVersion,
                        keycloakGroupId + "keycloak-adapter-core" + keycloakVersion,
                        keycloakGroupId + "keycloak-adapter-spi" + keycloakVersion,
                        keycloakGroupId + "keycloak-client-registration-api" + keycloakVersion)
                .withTransitivity().asFile();
    */
    /*
        File[] files = Maven.resolver()
                //.loadPomFromFile("pom.xml", "../app/pom.xml", "../persistence/pom.xml", "../pom.xml")
                .loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies()
                .resolve()
                .withTransitivity()
                .asFile();
     */

        File[] files = Maven.resolver()
                //.loadPomFromFile("pom.xml", "../app/pom.xml", "../persistence/pom.xml", "../pom.xml")
                .loadPomFromFile("../pom.xml")
                .importCompileAndRuntimeDependencies()
                //.importRuntimeAndTestDependencies()
                .resolve()
                .withTransitivity()
                .asFile();

        File[] filesKeycloak = Maven.resolver()
                .resolve(keycloakGroupId + "keycloak-core" + keycloakVersion,
                        keycloakGroupId + "keycloak-common" + keycloakVersion,
                        keycloakGroupId + "keycloak-adapter-core" + keycloakVersion,
                        keycloakGroupId + "keycloak-adapter-spi" + keycloakVersion,
                        keycloakGroupId + "keycloak-client-registration-api" + keycloakVersion)
                .withTransitivity().asFile();

        log.info("Dependency Files KeyCloak");

        for (File file : filesKeycloak) {
            log.info(file.getAbsolutePath());
        }

        log.info("Dependency Files");

        for (File file : files) {
            log.info(file.getAbsolutePath());
        }
        // File[] keycloak

        return ShrinkWrap.create(WebArchive.class, clazz.getSimpleName() + ".war")
                .addPackages(true, "cz.muni.fi.gag.web")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("web.xml", "WEB-INF/web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                //.addAsWebInfResource("beans.xml", "WEB-INF/beans.xml")
                .addAsLibraries(files)
                .addAsLibraries(filesKeycloak);
                //.addAsLibraries(files);
                //.addAsLibraries(resolver.artifact("cz.muni.fi.gag.web:gag-web-services"));
    }

    

    //@Deployment
    public static WebArchive getDeployment2(Class clazz) {
        //return getDeployment(AuthenticationTest.class);

        //try {
            File[] files = Maven.resolver()
                //.loadPomFromFile("pom.xml", "../app/pom.xml", "../persistence/pom.xml", "../pom.xml")
                .loadPomFromFile("pom.xml", "../services/pom.xml", "../pom.xml")
                .importRuntimeAndTestDependencies()
                .resolve()
                .withTransitivity()
                .asFile();

        /*
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                //.addAsWebInfResource("WEB-INF/beans.xml", "WEB-INF/beans.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                //.addAsWebInfResource("WEB-INF/jboss-web.xml", "WEB-INF/jboss-web.xml")
                .addAsWebInfResource("WEB-INF/web.xml", "WEB-INF/web.xml")
                //.addAsManifestResource("MANIFEST.MF")
                .addAsLibraries(files);
        return war;
        */

        return ShrinkWrap.create(WebArchive.class, clazz.getSimpleName() + ".war")
                .addPackages(true, "cz.muni.fi.gag.web")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("web.xml", "WEB-INF/web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                //.addAsLibraries(files);
                //.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                //.addAsWebInfResource("WEB-INF/jboss-web.xml", "WEB-INF/jboss-web.xml")
                //.addAsManifestResource("MANIFEST.MF")
                .addAsLibraries(files);
        /*

            WebArchive war = ShrinkWrap.create(WebArchive.class, clazz.getSimpleName() + ".war")
                .addPackages(true, "cz.muni.fi.gag.web")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                //.addAsWebInfResource("WEB-INF/web.xml", "WEB-INF/web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                //.addAsLibraries(files);
                //.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                //.addAsWebInfResource("WEB-INF/jboss-web.xml", "WEB-INF/jboss-web.xml")
                //.addAsManifestResource("MANIFEST.MF")
                .addAsLibraries(files);
        */
           // return war;
        
        /*} catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }*/
        //return null;
        /*
        String keycloakGroupId = "org.keycloak:";
        String keycloakVersion = ":6.0.1";

            File[] files = Maven.resolver()
                    .resolve(keycloakGroupId + "keycloak-core" + keycloakVersion,
                            keycloakGroupId + "keycloak-common" + keycloakVersion,
                            keycloakGroupId + "keycloak-adapter-core" + keycloakVersion,
                            keycloakGroupId + "keycloak-adapter-spi" + keycloakVersion,
                            keycloakGroupId + "keycloak-client-registration-api" + keycloakVersion)
                    .withTransitivity().asFile();

            log.info("Dependency Files");

            for (File file : files) {
                log.info(file.getAbsolutePath());
            }

            // File[] keycloak
            return ShrinkWrap.create(WebArchive.class, AuthenticationTest.class.getSimpleName() + ".war")
                    //.addAsWebInfResource("WEB-INF/beans.xml", "beans.xml")
                    //.addAsWebInfResource("WEB-INF/jboss-web.xml", "jboss-web.xml")
                    .addAsWebInfResource("WEB-INF/web.xml", "WEB-/web.xml")
                    .addAsWebInfResource("WEB-INF/keycloak.json", "WEB-INF/keycloak.json")
                    //.addAsManifestResource("MANIFEST.MF")
                    //.addAsLibraries(files)
                    .addPackages(true, "cz.muni.fi.gag.web")
                    .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                    .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsLibraries(files);
                    */
    }


    @Inject
    private UserDao userDao;

    @Inject
    private HandDeviceDao handDeviceDao;

    @Inject
    private FingerSensorOffsetDao fingerSensorOffsetDao;

    public static int thirdPartyIdCounter = 0;
    public static int deviceIdCounter = 0;

    public User buildUser() {
        User r = new User();
        r.setThirdPartyId("rand" + (++thirdPartyIdCounter));
        r.setRole(UserRole.USER);
        return r;
    }

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

    public FingerSensorOffset buildFingerSensorOffset(FingerPosition fingerPosition) {
        FingerSensorOffset r = new FingerSensorOffset();
        HandDevice hd = null;
        r.setDevice(hd);
        r.setSensorType(SensorType.QUAT_GYRO);
        r.setX((short) 0);
        r.setY((short) 0);
        r.setZ((short) 0);
        r.setPosition(fingerPosition);
        return r;
    }

    public FingerSensorOffset buildFingerSensorOffsetWithPersistentRefs(FingerPosition fingerPosition) {
        FingerSensorOffset r = buildFingerSensorOffset(fingerPosition);
        HandDevice d = buildHandDeviceAndPersist();
        r.setDevice(d);
        // fingerSensorOffsetDao.create(r);
        return r;
    }

    public WristSensorOffset buildWristSensorOffset() {
        WristSensorOffset r = new WristSensorOffset();

        HandDevice hd = null;
        r.setDevice(hd);
        r.setSensorType(SensorType.QUAT_GYRO);
        r.setX((short) 0);
        r.setY((short) 0);
        r.setZ((short) 0);

        return r;
    }

    public WristSensorOffset buildWristSensorOffsetWithPersistentRefs() {
        WristSensorOffset r = buildWristSensorOffset();
        HandDevice d = buildHandDeviceAndPersist();
        r.setDevice(d);
        return r;
    }

    public HandDevice buildHandDeviceAndPersist() {
        HandDevice r = buildHandDevice();
        r = handDeviceDao.create(r);
        return r;
    }

    HandDevice buildHandDevice() {
        User u = buildUser();
        u = userDao.create(u);

        List<SensorOffset> o = new ArrayList<>();
        for (FingerPosition fingerPosition : FingerPosition.values()) {
            o.add(buildFingerSensorOffset(fingerPosition));
        }

        o.add(buildWristSensorOffset());

        HandDevice r = new HandDevice();
        r.setDeviceId("devId" + (++deviceIdCounter));
        r.setUser(u);
        r.setOffsets(o);
        return r;
    }

}
