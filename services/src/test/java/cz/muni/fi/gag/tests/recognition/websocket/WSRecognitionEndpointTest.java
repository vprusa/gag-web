package cz.muni.fi.gag.tests.recognition.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.gag.tests.common.FileLogger;
import cz.muni.fi.gag.tests.endpoint.GestureEndpointTest;
import cz.muni.fi.gag.tests.endpoint.websocket.WSDataLineEndpointTest;
import cz.muni.fi.gag.tests.endpoint.websocket.WSDataLineEndpointTestBase;
import cz.muni.fi.gag.tests.endpoint.websocket.WSEndpointClientJSONObject;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.services.filters.RecordedDataFilter;
import cz.muni.fi.gag.web.services.filters.RecordedDataFilterImpl;
import cz.muni.fi.gag.web.services.recognition.GestureMatcher;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.ActionEncoderBase;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.PlayerActions;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.RecognitionActions;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * @author Vojtech Prusa
 */
@RunWith(Arquillian.class)

public class WSRecognitionEndpointTest extends WSDataLineEndpointTestBase {
//public class WSRecognitionEndpointTest extends GestureEndpointTest {
//public class WSRecognitionEndpointTest extends AuthenticationTestBase {
// And so I got to the point when arquillian debugging would be nice
// http://arquillian.org/guides/getting_started_rinse_and_repeat/#debug_a_managed_server

    public static Logger log = FileLogger.getLogger(WSRecognitionEndpointTest.class.getSimpleName());

//    public static final String TESTED_ENDPOINT = "ws://" + URL_NO_PROTOCOL + WSDataLineEndpointTest.class.getSimpleName() + "/datalinews/";
    public static final String TESTED_ENDPOINT = "ws://" + URL_NO_PROTOCOL + "gagweb/datalinews";

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(WSDataLineEndpointTest.class);
    }

//    @Test
//    @RunAsClient
    /*
    public void testEndpointJSONObject() throws Exception {
        Session session = connectToServer(TESTED_ENDPOINT);
        log.info(session.toString());
        boolean latchWait = WSEndpointClientJSONObject.latch.await(3, TimeUnit.SECONDS);
        log.info("latchWait: " + latchWait);
        log.info(WSEndpointClientJSONObject.response.toString());
        //assertEquals(JSON, MyEndpointClientJSONObject.response);
    }
     */

    /* *
     * Method used to supply connection to the server by passing the naming of
     * the websocket endpoint
     *
     * @return
     * @throws DeploymentException
     * @throws IOException
     * @throws URISyntaxException
     */
    /*
    public Session connectToServer() throws Exception {
        log.info("connectToServer");
        // https://www.programcreek.com/java-api-examples/?api=javax.websocket.ClientEndpointConfig

        ClientEndpointConfig.Configurator configurator = new ClientEndpointConfig.Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                try {
                    String accessToken = basicLogin();
                    headers.put("Authorization", Arrays.asList("Bearer " + accessToken));
                    log.info(accessToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterResponse(HandshakeResponse hr) {
                super.afterResponse(hr);
                log.info(hr.toString());
            }
        };
        ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        log.info("Connecting to URI: " + TESTED_ENDPOINT);
        URI uri = new URI(TESTED_ENDPOINT);
        Session session = container.connectToServer(WSEndpointClientJSONObject.class, clientEndpointConfig, uri);

        /*
        MessageHandler.Whole<String> mhw = new MessageHandler.Whole<String>() {

            @Override
            public void onMessage(String text) {
                / *try {
                  remote.sendText("Got your message (" + text + "). Thanks !");
                } catch (IOException ioe) {
                }* /
                //log.info("onMessage");
                log.info(text);
                WSEndpointClientJSONObject.response.add(text);
            }

        };
        session.addMessageHandler(mhw);
        */
//        return session;
//    }

    /*
    @Deployment
    public static WebArchive deployment() {
        Class clazz = RecognitionEndpointTest.class;

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
     */

//    @Inject
//    public GestureService gestureService;
//
//    @Inject
//    public DataLineService dataLineService;

//    @Inject
//    public RecordedDataFilterImpl rdf;
    public RecordedDataFilter rdf;

    @Test
    @RunAsClient
    public void testGestureRecognitionMatch() throws Exception {
        log.info("testRecordedDataFilter");
//        Long gId = 21L;
        // For testing purposes lets compare 2 filtered gestures and as ref gesture use more filtered one.
        Long gId = 36L;
        Long gIdRef = 35L;
//        log.info((gestureService == null ? "gestureService is null" : "gestureService: " + gestureService.toString()));
        rdf = new RecordedDataFilterImpl();
        log.info((rdf == null ? "rdf is null" : "rdf: " + rdf.toString()));
//        assertNull("gestureService: null", gestureService);
        assertNotNull("rdf: null", rdf);

//        GestureEndpointTest get = new GestureEndpointTest();
        HttpClient client = new DefaultHttpClient();
        Session session = connectToServer(TESTED_ENDPOINT);

        Gesture gRef = GestureEndpointTest.getGesture(client, basicLogin());
        assertNotNull("gRef: " + gRef.toString(), gRef);

        assertNotNull("session: " + session.toString(), session);

        MessageHandler.Whole<String> mhw = new MessageHandler.Whole<String>() {
            ActionEncoderBase aeb = new ActionEncoderBase(RecognitionActions.class);
            ObjectMapper mapper;

            @Override
            public void onMessage(String text) {
                /*try {
                    remote.sendText("Got your message (" + text + "). Thanks !");
                } catch (IOException ioe) {
                }*/
                //log.info("onMessage");
                log.info(text);
                List<GestureMatcher> lgm = null;
                try {
                    lgm = mapper.reader().forType(List.class).readValue(text);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                log.info("lgm");
                log.info((lgm == null ? "lgm is null" : lgm.toString()));

//                WSEndpointClientJSONObject.response.add(text);
            }
        };
        session.addMessageHandler(mhw);
        final RemoteEndpoint.Basic remote = session.getBasicRemote();

        ActionEncoderBase aeb = new ActionEncoderBase(PlayerActions.class);
        PlayerActions ra = new PlayerActions();
        ra.setAction(PlayerActions.ActionsEnum.PLAY);
        ra.setGestureId(gIdRef);
        log.info("Action ra");
        log.info(ra.toString());
        String raStr = aeb.encode(ra);

        remote.sendText(raStr);
         ra = new PlayerActions();
        ra.setAction(PlayerActions.ActionsEnum.PLAY);
        ra.setGestureId(gIdRef);
        log.info("Action ra");
        log.info(ra.toString());
         raStr = aeb.encode(ra);

        remote.sendText(raStr);


        /*
        ActionEncoderBase aeb = new ActionEncoderBase(RecognitionActions.class);
        RecognitionActions ra = new RecognitionActions();
        ra.setAction(RecognitionActions.ActionsEnum.START);
        log.info("Action ra");
        log.info(ra.toString());
        String raStr = aeb.encode(ra);
        remote.sendText(raStr);
         */

        /*
         send commands
         - start_recognition
         - same dl as fRef
         */

        boolean latchWait = WSEndpointClientJSONObject.latch.await(5, TimeUnit.SECONDS);
        log.info("latchWait: " + latchWait);
        log.info(WSEndpointClientJSONObject.response.toString());

        /*
        Optional<Gesture> gOpt = gestureService.findById(gId);
        Optional<Gesture> gRefOpt = gestureService.findById(gIdRef);
        assertTrue("Gesture is not present", gOpt.isPresent());
        assertTrue("Ref Gesture is not present", gRefOpt.isPresent());

        Gesture g = gOpt.get();
        Gesture gRef = gRefOpt.get();

        List<FingerDataLine> l = ((List<FingerDataLine>) g.getData()).stream().filter(dl -> dl.getPosition().equals(Sensor.INDEX)).collect(Collectors.toList());
        List<FingerDataLine> lRef = ((List<FingerDataLine>) gRef.getData()).stream().filter(dl -> dl.getPosition().equals(Sensor.INDEX)).collect(Collectors.toList());
        DataLineGestureIterator dlgIter = dataLineService.buildIteratorByGesture(gRef.getId());
        SensorComparator sgi = new SensorComparator<FingerDataLine>(Sensor.INDEX, gRef, dlgIter);

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
         */

    }

}
