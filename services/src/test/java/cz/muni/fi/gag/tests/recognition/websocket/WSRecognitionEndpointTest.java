package cz.muni.fi.gag.tests.recognition.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import cz.muni.fi.gag.tests.common.FileLogger;
import cz.muni.fi.gag.tests.endpoint.GestureEndpointTest;
import cz.muni.fi.gag.tests.endpoint.websocket.WSEndpointTestBase;
import cz.muni.fi.gag.tests.endpoint.websocket.WSReplayerEndpointTest;
import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.WristDataLine;
import cz.muni.fi.gag.web.services.filters.RecordedDataFilter;
import cz.muni.fi.gag.web.services.filters.RecordedDataFilterImpl;
import cz.muni.fi.gag.web.services.recognition.matchers.SingleSensorGestureMatcher;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.Action;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.ActionDecoder;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.ActionEncoderBase;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.actions.RecognitionActions;
import cz.muni.fi.gag.web.services.websocket.endpoint.packet.datalines.*;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * @author Vojtech Prusa
 *
 * mvn clean install test -Dtest=WSRecognitionEndpointTest -Dcheckstyle.skip | tee test.log
 *
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

    public ClientEndpointConfig getEndpointConfig(){
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
        return clientEndpointConfig;
    }

    @Override
    public Session connectToServer(final Class endpointClass, final String uriStr) throws Exception {
        log.info("connectToServer");
        // https://www.programcreek.com/java-api-examples/?api=javax.websocket.ClientEndpointConfig
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        log.info("Connecting to URI: " + uriStr);
        URI uri = new URI(uriStr);
        Session session = container.connectToServer(endpointClass, getEndpointConfig(), uri);
        return session;
    }

    public RecordedDataFilter rdf;

    @Test
    @RunAsClient
    public void testGestureRecognitionMatch() throws Exception {
        log.info("testRecordedDataFilter");
//        Long gId = 21L;
        // For testing purposes lets compare 2 filtered gestures and as ref gesture use more filtered one.
        Long gId = 79L;
        Long gIdRef = 79L; // 35 72 79
//        log.info((gestureService == null ? "gestureService is null" : "gestureService: " + gestureService.toString()));
        rdf = new RecordedDataFilterImpl();
        log.info((rdf == null ? "rdf is null" : "rdf: " + rdf.toString()));
        assertNotNull("rdf: null", rdf);

//        GestureEndpointTest get = new GestureEndpointTest();
        HttpClient client = new DefaultHttpClient();
//        Session session = connectToServer(WSRecognitionClientEndpoint.class, TESTED_ENDPOINT);

        log.info("connectToServer");
        // https://www.programcreek.com/java-api-examples/?api=javax.websocket.ClientEndpointConfig
        Gesture gRef = GestureEndpointTest.getGesture(gIdRef, client, basicLogin());
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        log.info("Connecting to URI: " + TESTED_ENDPOINT);
        WSRecognitionClientEndpoint wsrce = new WSRecognitionClientEndpoint(gRef);
        Session session = container.connectToServer(wsrce, getEndpointConfig(), new URI(TESTED_ENDPOINT));

        assertNotNull("gRef: " + gRef.toString(), gRef);
        assertNotNull("session: " + session.toString(), session);

        MessageHandler.Whole<String> mhw = new MessageHandler.Whole<String>() {
            ActionEncoderBase aeb = new ActionEncoderBase(RecognitionActions.class);
            ObjectMapper mapper;
            ActionDecoder<RecognitionActions> rad = new ActionDecoder(RecognitionActions.class, Action.ActionsTypesEnum.RECOGNITION);
            DataLineEncoder dle = new DataLineEncoder();
            FingerDataLineEncoder fdle = new FingerDataLineEncoder();
            WristDataLineEncoder wdle = new WristDataLineEncoder();
            DataLineDecoder dld = new DataLineDecoder();
            FingerDataLineDecoder fdld = new FingerDataLineDecoder();
            WristDataLineDecoder wdld = new WristDataLineDecoder();

//            DataLineEncoder.class, FingerDataLineEncoder.class, WristDataLineEncoder.class

            @Override
            public void onMessage(String msg) {
                log.info("onMessage");
                log.info(msg);

                if (rad.willDecode(msg)) {
                    log.info("rad");
                    RecognitionActions ra = (RecognitionActions) rad.decode(msg);
                    switch (ra.getAction()) {
                        case START: {
                            log.info("ack: START");
                            // TODO start sending messages

                            log.info("onMessage: gRef.getData()");
                            List<Map<String,String>> dll = gRef.getData();
                            log.info("onMessage: gRef.getData");
                            log.info(gRef.getData().toString());
                            log.info(gRef.getData().get(0).toString());
                            Iterator<Map<String,String>> dlli = dll.iterator();
                            log.info(dlli.toString());
                            ObjectWriter objectWriter = new ObjectMapper().writer();

                            try {
                                while (dlli.hasNext()) {
                                log.info("dl hasNext: ");
                                Map<String,String> dl = dlli.next();
//                                session.getBasicRemote().sendText(data);
//                                Session newSession = container.connectToServer(wsrce, getEndpointConfig(), new URI(TESTED_ENDPOINT));

                                log.info("dl type: " + dl.getClass() + " data: " + dl.toString());
                                try {
                                    String dataIn = objectWriter.writeValueAsString(dl);
                                    String dataOut = "";
                                    if(wdld.willDecode(dataIn)){
                                        WristDataLine wdl = wdld.decode(dataIn);
                                        dataOut = wdle.encode(wdl);
                                    }else if(fdld.willDecode(dataIn)){
                                        FingerDataLine fdl = fdld.decode(dataIn);
                                        dataOut = fdle.encode(fdl);
                                    }else if(dld.willDecode(dataIn)){
                                        DataLine dlOut = dld.decode(dataIn);
                                        dataOut = dle.encode(dlOut);
                                    }
                                    log.info("dataOut: " + dataOut);

                                    session.getBasicRemote().sendText(dataOut);

                                } catch (IOException e) {
                                    log.info(e.getMessage());
                                    e.printStackTrace();
                                } catch (EncodeException e) {
                                    log.info(e.getMessage());
                                    e.printStackTrace();
                                }
                                }
                            } catch (Exception e) {
                                log.info(e.getMessage());
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                } else {
                    // lets consider everything else of type List<GestureMatcher>
                    List<SingleSensorGestureMatcher> lgm = null;
                    try {
                        lgm = mapper.reader().forType(List.class).readValue(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    log.info((lgm == null ? "lgm is null" : "lgm: " + lgm.toString()));

                    WSRecognitionClientEndpoint.response.add(msg);

                    /*
                    ActionEncoderBase aeb = new ActionEncoderBase(RecognitionActions.class);
                    RecognitionActions ra = new RecognitionActions();
                    ra.setAction(RecognitionActions.ActionsEnum.START);
                    log.info("Action ra");
                    log.info(ra.toString());
                    String raStr = aeb.encode(ra);
                    session.getBasicRemote().sendText(raStr);
                     */

                }
            }

        };
        session.addMessageHandler(mhw);

        long latchTimeout = 20;
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
