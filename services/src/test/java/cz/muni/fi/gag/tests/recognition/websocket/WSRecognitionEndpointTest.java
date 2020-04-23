package cz.muni.fi.gag.tests.recognition.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.gag.tests.common.FileLogger;
import cz.muni.fi.gag.tests.endpoint.GestureEndpointTest;
import cz.muni.fi.gag.tests.endpoint.websocket.WSEndpointTestBase;
import cz.muni.fi.gag.tests.endpoint.websocket.WSReplayerEndpointTest;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.services.filters.RecordedDataFilter;
import cz.muni.fi.gag.web.services.filters.RecordedDataFilterImpl;
import cz.muni.fi.gag.web.services.recognition.GestureMatcher;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.ActionEncoderBase;
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

public class WSRecognitionEndpointTest extends WSEndpointTestBase {
// http://arquillian.org/guides/getting_started_rinse_and_repeat/#debug_a_managed_server

    public static Logger log = FileLogger.getLogger(WSRecognitionEndpointTest.class.getSimpleName());

    public static final String TESTED_ENDPOINT = "ws://" + URL_NO_PROTOCOL + "gagweb/datalinews";

    @Deployment
    public static WebArchive deployment() {
        return getDeployment(WSReplayerEndpointTest.class);
    }

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
        assertNotNull("rdf: null", rdf);

//        GestureEndpointTest get = new GestureEndpointTest();
        HttpClient client = new DefaultHttpClient();
        Session session = connectToServer(WSRecognitionClientEndpoint.class, TESTED_ENDPOINT);

        Gesture gRef = GestureEndpointTest.getGesture(gIdRef, client, basicLogin());
        assertNotNull("gRef: " + gRef.toString(), gRef);

        assertNotNull("session: " + session.toString(), session);

        MessageHandler.Whole<String> mhw = new MessageHandler.Whole<String>() {
            ActionEncoderBase aeb = new ActionEncoderBase(RecognitionActions.class);
            ObjectMapper mapper;

            @Override
            public void onMessage(String text) {
                log.info("onMessage");
                log.info(text);
                List<GestureMatcher> lgm = null;
                try {
                    lgm = mapper.reader().forType(List.class).readValue(text);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                log.info("lgm");
                log.info((lgm == null ? "lgm is null" : lgm.toString()));

                WSRecognitionClientEndpoint.response.add(text);
            }
        };
        session.addMessageHandler(mhw);

        long latchTimeout = 5;
        log.info("latchTimeout: " + latchTimeout);
        boolean latchWait = WSRecognitionClientEndpoint.latch.await(latchTimeout, TimeUnit.SECONDS);
        log.info("latchWait: " + latchWait);
        log.info(WSRecognitionClientEndpoint.response.toString());

//        final RemoteEndpoint.Basic remote = session.getBasicRemote();
        /*
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
        */

        /*
         send commands
         - start_recognition
         - same dl as fRef
         */

    }

}
