package cz.muni.fi.gag.tests.recognition;

import cz.muni.fi.gag.tests.common.FileLogger;
import cz.muni.fi.gag.tests.common.TestServiceBase;
import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureSensorIterator;
import cz.muni.fi.gag.web.persistence.entity.*;
import cz.muni.fi.gag.web.services.filters.RecordedDataFilterImpl;
import cz.muni.fi.gag.web.services.recognition.matchers.MultiSensorGestureMatcher;
import cz.muni.fi.gag.web.services.recognition.matchers.SingleSensorGestureMatcher;
import cz.muni.fi.gag.web.services.recognition.comparators.HandComparator;
import cz.muni.fi.gag.web.services.recognition.comparators.SensorComparator;
import cz.muni.fi.gag.web.services.service.DataLineService;
import cz.muni.fi.gag.web.services.service.GestureService;
import cz.muni.fi.gag.web.services.service.UserService;
import cz.muni.fi.gag.web.services.websocket.service.GestureRecognizer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * @author Vojtech Prusa
 * <p>
 * mvn clean install test -Dtest=RecognitionTest -Dcheckstyle.skip | tee test.log
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
    public DataLineService dataLineService;

    @Inject
    public UserService userService;

    @Inject
    public RecordedDataFilterImpl rdf;

    @Inject
    public GestureRecognizer gr;

    //    @Test
    public void testNoSensorGestureRecognizedMatched() {
        log.info("testGestureRecognizedNoneSensorMatched");
        Long gId = 19L;
        Long gIdRef = 35L;
        testEverySensorGestureRecognitionMatchFor(gId, gIdRef);
    }

    //    @Test
    public void testEverySensorGestureRecognizedMatched() {
        log.info("testGestureRecognizedEverySensorMatched");
        Long gId = 21L;
        Long gIdRef = 35L;
        testEverySensorGestureRecognitionMatchFor(gId, gIdRef);
    }

    public void testEverySensorGestureRecognitionMatchFor(Long gId, Long gIdRef) {
        testEverySensorGestureRecognitionMatchFor(gId, gIdRef, true);
    }

    public void testEverySensorGestureRecognitionMatchFor(Long gId, Long gIdRef, boolean shouldMatch) {
        Optional<Gesture> gOpt = gestureService.findById(gId);
        Optional<Gesture> gRefOpt = gestureService.findById(gIdRef);
        assertTrue("Gesture is not present", gOpt.isPresent());
        assertTrue("Ref Gesture is not present", gRefOpt.isPresent());

        Gesture g = gOpt.get();
        Gesture gRef = gRefOpt.get();

        List<FingerDataLine> gData = g.getData();
//        HandComparator hgi = new HandComparator(gRef, dataLineService);

        DataLineGestureSensorIterator dlgsIters[] = new DataLineGestureSensorIterator[6];
        for (int i = 0; i < Sensor.values().length; i++) {
            DataLineGestureSensorIterator dlgsIter = dataLineService.buildIterator(gRef.getId(), Sensor.values()[i]);
            dlgsIters[i] = dlgsIter;
        }
        HandComparator hgi = new HandComparator(gRef, dlgsIters);
        MultiSensorGestureMatcher matches = null;
//        List<GestureMatcher[]> handMatches = new GestureMatcher[Sensor.values().length]
        List<MultiSensorGestureMatcher[]> handMatches = new ArrayList<MultiSensorGestureMatcher[]>();

        Iterator<FingerDataLine> gDataIter = gData.iterator();
        while (gDataIter.hasNext()) {
            DataLine dl = gDataIter.next();
            FingerDataLine fdl = (FingerDataLine) dl;
//            log.info(fdl.toString());
            matches = hgi.compare(fdl);
            if (matches != null && !matches.isEmpty()) {
                log.info("Found gesture match at: " + matches);
//                handMatches[matches.getAtDataLine().getPosition().ordinal()] = matches;
//                handMatches[matches.get(0).getAtDataLine().getPosition().ordinal()] = matches.get(0);
                try {
//                    log.info("Found gesture match at: " + matches.stream().map(MultiSensorGestureMatcher::toString)
//                            .collect(Collectors.joining(";;;")));
//
//                    log.info("Found gesture match at groupingBy: " + matches.stream().map(MultiSensorGestureMatcher::getG)
//                            .collect(Collectors.groupingBy(Gesture::getId)).toString());

//                    log.info("Found gesture match at groupingBy2: " + matches.stream().map(MultiSensorGestureMatcher::getG)
//                            .collect(Collectors.groupingBy(Gesture::getId)));

//                    log.info("Found gesture match at groupingBy3: " + matches.stream().collect(Collectors.groupingBy(MultiSensorGestureMatcher::getG)));

//                    map(GestureMatcher::getG::).collect(Collectors.groupingBy(Gesture::getId)).entrySet().stream().map(Gesture::toString).collect(Collectors.joining(";;;").toString()));

                } catch (Exception e) {
//                            log.info(e.stac);
                    e.printStackTrace();
                }
/*
                Iterator<MultiSensorGestureMatcher> mi = matches.iterator();
                while (mi.hasNext()) {
                    MultiSensorGestureMatcher gm = mi.next();
                    MultiSensorGestureMatcher[] gma = new MultiSensorGestureMatcher[Sensor.values().length];
                    if (gm.keySet().iterator().hasNext()) {
                        Sensor s = gm.keySet().iterator().next();
                        gma[gm.get(s).getAtDataLine().getPosition().ordinal()] = gm;
                        handMatches.add(gma);
                    } else {
                        log.info("No sensor key found!!!");
                    }

                }
 */
//                break;
            }
        }


        for (Iterator<MultiSensorGestureMatcher[]> hmi = handMatches.iterator(); hmi.hasNext(); ) {
            MultiSensorGestureMatcher[] gma = hmi.next();
            log.info("" + gma.toString());
            for (Sensor s : Sensor.values()) {
                log.info("   " + gma[s.ordinal()].toString());
            }
        }

        /*
        Iterator<GestureMatcher[]> hmi = handMatches.iterator();
        while(hmi.hasNext()) {
            GestureMatcher[] gma = hmi.next();
            for (int i = 0; i < Sensor.values().length; i++) {
//                assertTrue((shouldMatch ? "None" : "Some") + " match detected at position " + Sensor.values()[i] + ", all matches: "
//                        + (Arrays.toString(gma)), ((shouldMatch && gma[i] != null)
//                        || (!shouldMatch && gma[i] == null)));
                log.info((shouldMatch ? "None" : "Some") + " match detected at position " + Sensor.values()[i]
                        + ", all matches: " + (Arrays.toString(gma)));
                // TODO assert
            }
        }*/
    }

    //    @Test
//    @RunAsClient
    public void testIndexGestureRecognitionMatch() {
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

        Sensor s = Sensor.INDEX;

        List<FingerDataLine> l = ((List<FingerDataLine>) g.getData()).stream().filter(dl -> dl.getPosition().equals(Sensor.INDEX)).collect(Collectors.toList());
        List<FingerDataLine> lRef = ((List<FingerDataLine>) gRef.getData()).stream().filter(dl -> dl.getPosition().equals(Sensor.INDEX)).collect(Collectors.toList());
        DataLineGestureSensorIterator dlgIter = (DataLineGestureSensorIterator) dataLineService.buildIterator(gRef.getId(), s);
        SensorComparator sgi = new SensorComparator<FingerDataLine>(s, gRef, dlgIter);

        List<SingleSensorGestureMatcher> matches = null;

        Iterator<FingerDataLine> iter = l.iterator();
        while (iter.hasNext()) {
            DataLine dl = iter.next();
            if (dl instanceof FingerDataLine) {
                FingerDataLine fdl = (FingerDataLine) dl;
                if (fdl.getPosition().equals(Sensor.INDEX)) {
                    matches = sgi.compare(fdl);
                    if (matches != null) {
                        log.info("Found gesture match at: " + matches.toString());
                        break;
                    }
                }
            } else if (dl instanceof WristDataLine) {
            }
        }
    }

    //    @Test
    public void testEverySensorGestureRecognizedMatched6OnRight() {
        log.info("testEverySensorGestureRecognizedMatched6OnRight");
//        Long gId = 72L; // 79 72
//        Long gIdRef = 72L;
        Long gId = 72L; // 79 72
        Long gIdRef = 72L;
        testEverySensorGestureRecognitionMatchFor(gId, gIdRef);
    }

    @Test
    public void gestureRecognizerTest() {
        long gId = 72;
        long gIdRef = 72;

        // TODO get user via service

//        +----+------+-----------------------+
//        | id | role | thirdPartyId          |
//        +----+------+-----------------------+
//        |  1 |    1 | email:admin2@test.com |
//        | 18 |    1 | email:test@test.com   |

        long userId = 1l;

        User u = userService.findById(userId).get();
//        u.setRole(UserRole.ADMIN);
//        u.setThirdPartyId("email:admin2@test.com");

        Optional<Gesture> gOpt = gestureService.findById(gId);
        Optional<Gesture> gRefOpt = gestureService.findById(gIdRef);
        assertTrue("Gesture is not present", gOpt.isPresent());
        assertTrue("Ref Gesture is not present", gRefOpt.isPresent());

        Gesture g = gOpt.get();
        Gesture gRef = gRefOpt.get();

        {
            List<FingerDataLine> gData = g.getData();
//        HandComparator hgi = new HandComparator(gRef, dataLineService);

            DataLineGestureSensorIterator dlgsIters[] = new DataLineGestureSensorIterator[6];
            for (int i = 0; i < Sensor.values().length; i++) {
                DataLineGestureSensorIterator dlgsIter = dataLineService.buildIterator(gRef.getId(),
                        Sensor.values()[i]);
                dlgsIters[i] = dlgsIter;
            }
            HandComparator hgi = new HandComparator(gRef, dlgsIters);
            List<MultiSensorGestureMatcher> matches = null;
            List<MultiSensorGestureMatcher[]> handMatches = new ArrayList<MultiSensorGestureMatcher[]>();
            gr.start(u);
            for (Iterator<FingerDataLine> gDataIter = gData.iterator(); gDataIter.hasNext(); ) {
                DataLine dl = gDataIter.next();
                log.info("gestureRecognizerTest.dl: " + dl.toString());
                List<MultiSensorGestureMatcher> recognize = gr.recognize(dl);
                log.info("gestureRecognizerTest.recognize: " + recognize.toString());
            }
        }
    }

}
