package cz.muni.fi.gag.tests.recognition;

import cz.muni.fi.gag.tests.common.FileLogger;
import cz.muni.fi.gag.tests.common.TestServiceBase;
import cz.muni.fi.gag.web.persistence.entity.*;
import cz.muni.fi.gag.web.services.filters.RecordedDataFilterImpl;
import cz.muni.fi.gag.web.services.recognition.GestureMatcher;
import cz.muni.fi.gag.web.services.recognition.comparators.HandComparator;
import cz.muni.fi.gag.web.services.recognition.comparators.SensorComparator;
import cz.muni.fi.gag.web.services.service.GestureService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author Vojtech Prusa
 */
@RunWith(Arquillian.class)
public class RecognitionTest extends TestServiceBase {
// And so I got to the point when arquillian debugging would be nice
// http://arquillian.org/guides/getting_started_rinse_and_repeat/#debug_a_managed_server

    public static Logger log = FileLogger.getLogger(RecognitionTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
        Class clazz = RecognitionTest.class;

        File[] filesKeycloak = Maven.resolver()
                .resolve(keycloakGroupId + "keycloak-core" + keycloakVersion,
                        keycloakGroupId + "keycloak-common" + keycloakVersion,
                        keycloakGroupId + "keycloak-adapter-core" + keycloakVersion,
                        keycloakGroupId + "keycloak-adapter-spi" + keycloakVersion,
                        keycloakGroupId + "keycloak-client-registration-api" + keycloakVersion)
                .withTransitivity().asFile();

        // https://stackoverflow.com/questions/52200635/arquillian-runclient-test-as-remote
        return ShrinkWrap.create(WebArchive.class, clazz.getSimpleName() + ".war")
                .addPackages(true, "cz.muni.fi.gag.web.persistence")
                .addPackages(true, "cz.muni.fi.gag.web.services")
                .addPackages(true, "cz.muni.fi.gag.tests.common")
                .addPackages(true, "cz.muni.fi.gag.tests.filters")
                .addPackages(true, "cz.muni.fi.gag.tests.recognition")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebResource("index-test.html", "index.html")
                .addAsWebResource("import-test-live.sql", "import.sql")
                // TODO fix testing web.xml so it would either work with KC or with some other managing of roles
                .addAsWebInfResource("web-test.xml", "web.xml")
                .addAsWebInfResource("keycloak-test.json", "keycloak.json")
//                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("beans-test.xml", "beans.xml")
                .addAsLibraries(filesKeycloak);
    }

    @Inject
    public GestureService gestureService;

    @Inject
    public RecordedDataFilterImpl rdf;

    @Test
    public void testGestureRecognizedNoneSensorMatched() {
        log.info("testGestureRecognizedNoneSensorMatched");
        Long gId = 19L;
        Long gIdRef = 35L;
        testGestureEverySensorRecognitionMatchFor(gId,gIdRef);
    }

//    @Test
    public void testGestureRecognizedEverySensorMatched() {
        log.info("testGestureRecognizedEverySensorMatched");
        Long gId = 21L;
        Long gIdRef = 35L;
        testGestureEverySensorRecognitionMatchFor(gId,gIdRef);
    }

    public void testGestureEverySensorRecognitionMatchFor(Long gId,  Long gIdRef) {
        Optional<Gesture> gOpt = gestureService.findById(gId);
        Optional<Gesture> gRefOpt = gestureService.findById(gIdRef);
        assertTrue("Gesture is not present", gOpt.isPresent());
        assertTrue("Ref Gesture is not present", gRefOpt.isPresent());

        Gesture g = gOpt.get();
        Gesture gRef = gRefOpt.get();

        List<FingerDataLine> l = g.getData();
        HandComparator hgi = new HandComparator(gRef);
        GestureMatcher match = null;

        GestureMatcher[] handMatches = new GestureMatcher[Sensor.values().length];

        Iterator<FingerDataLine> iter = l.iterator();
        while (iter.hasNext()) {
            DataLine dl = iter.next();
            FingerDataLine fdl = (FingerDataLine) dl;
            match = hgi.compare(fdl);
            if (match != null) {
                log.info("Found gesture match at: " + match);
                handMatches[match.getAtDataLine().getPosition().ordinal()] = match;
//                break;
            }
        }

        for (int i = 0; i < Sensor.values().length; i++) {
            assertNull("Some match detected at position " + Sensor.values()[i] + ", all matches: " + (Arrays.toString(handMatches)), handMatches[i]);
        }
    }

    //    @Test
//    @RunAsClient
    public void testGestureIndexRecognitionMatch() {
        log.info("testRecordedDataFilter");
//        Long gId = 21L;
        // For testing purposes lets compare 2 filtered gestures and as ref gesture use more filtered one.
        Long gId = 36L;
        Long gIdRef = 35L;

        log.info((gestureService == null ? "gestureService is null" : "gestureService: " + gestureService.toString()));
        log.info((rdf == null ? "rdf is null" : "rdf: " + rdf.toString()));

        Optional<Gesture> gOpt = gestureService.findById(gId);
        Optional<Gesture> gRefOpt = gestureService.findById(gIdRef);
        assertTrue("Gesture is not present", gOpt.isPresent());
        assertTrue("Ref Gesture is not present", gRefOpt.isPresent());

        Gesture g = gOpt.get();
        Gesture gRef = gRefOpt.get();

        List<FingerDataLine> l = ((List<FingerDataLine>) g.getData()).stream().filter(dl -> dl.getPosition().equals(Sensor.INDEX)).collect(Collectors.toList());
        List<FingerDataLine> lRef = ((List<FingerDataLine>) gRef.getData()).stream().filter(dl -> dl.getPosition().equals(Sensor.INDEX)).collect(Collectors.toList());
        SensorComparator sgi = new SensorComparator<FingerDataLine>(Sensor.INDEX, gRef);
        GestureMatcher match = null;

        Iterator<FingerDataLine> iter = l.iterator();
        while (iter.hasNext()) {
            DataLine dl = iter.next();
            if (dl instanceof FingerDataLine) {
                FingerDataLine fdl = (FingerDataLine) dl;
                if (fdl.getPosition().equals(Sensor.INDEX)) {
                    match = sgi.compare(fdl);
                    if (match != null) {
                        log.info("Found gesture match at: " + match);
                        break;
                    }
                }
            } else if (dl instanceof WristDataLine) {
            }
        }

    }

}
