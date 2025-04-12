package cz.gag.web.services.websocket.service;

import cz.gag.web.persistence.dao.impl.DataLineGestureSensorIterator;
import cz.gag.web.persistence.entity.*;
import cz.gag.web.services.logging.Log;
import cz.gag.web.services.recognition.comparators.HandComparator;
import cz.gag.web.services.recognition.matchers.MultiSensorGestureMatcher;
import cz.gag.web.services.recognition.matchers.SingleSensorGestureMatcher;
import cz.gag.web.services.service.DataLineService;
import cz.gag.web.services.service.GestureService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

import static cz.gag.web.services.websocket.service.GestureRecognizer.RecognizerState.IDLE;
import static cz.gag.web.services.websocket.service.GestureRecognizer.RecognizerState.RECOGNIZING;

/**
 * @author Vojtech Prusa
 * <p>
 * TODO for bean type change take a look at TODO
 * {@link DataLineRePlayer}
 */
@Named
@ApplicationScoped
//@Singleton
public class GestureRecognizer/* implements Serializable*/ {

    public static final Log.TypedLogger log = new Log.TypedLogger<Log.LoggerTypeWSRecognizer>(Log.LoggerTypeWSRecognizer.class);

    private RecognizerState state = IDLE;

    @Inject
    private DataLineService dataLineService;

    @Inject
    private GestureService gestureService;

    // TODO fix re-init with start()
    protected List<MultiSensorGestureMatcher> recognizedGestures;
    protected List<HandComparator> handComparators;

    public boolean isRecognizing() {
        return getState() == RECOGNIZING;
    }

    // preparing for further states
    public enum RecognizerState {IDLE, RECOGNIZING}

    public RecognizerState getState() {
        synchronized (state) {
            return state;
        }
    }

    public void start(User current) {
        synchronized (state) {
            this.state = RECOGNIZING;
            // TODO refactor...
            // TODO check if this is necessary
            // TODO store results
            // TODO log metrics
            handComparators = new ArrayList<HandComparator>();

            recognizedGestures = new ArrayList<MultiSensorGestureMatcher>();
            log.info("for current: " + current.toString());
            List<Gesture> lGOpts = gestureService.findActive(current);
            log.info("for lGOpts: " + lGOpts.toString());

            for (Iterator<Gesture> git = lGOpts.iterator(); git.hasNext(); ) {
                Gesture g = git.next();
                log.info("for gesturesIters: " + g.toString());
//                DataLineGestureIterator dlgIter = dataLineService.buildIteratorByGesture(g.getId());
                DataLineGestureSensorIterator dlgsIters[] = new DataLineGestureSensorIterator[6];
                for (int i = 0; i < Sensor.values().length; i++) {
                    DataLineGestureSensorIterator dlgsIter = dataLineService.buildIterator(g.getId(), Sensor.values()[i]);
                    dlgsIters[i] = dlgsIter;
                }
                handComparators.add(new HandComparator(g, dlgsIters));
                // TODO add
//                gesturesIters.put(g, dlgsIters);
            }
            log.info("handComparators: " + handComparators.toString());
        }
    }

    public void stop() {
        synchronized (state) {
            this.state = IDLE;
        }
    }

    public List<MultiSensorGestureMatcher> recognize(DataLine dl) {
        Log.info("Recognize: " + dl.toString());
        switch (getState()) {
            case IDLE: {
            }
            break;
            case RECOGNIZING: {
                log.info("case: RECOGNIZING");

                // get rid of previous gestures
                // TODO brainstorm: idk if it is needed/wanted to keep data till next iteration?
                // -- same goes for GestureCollector.collect.{mgm.clear() ... return mgm; }
                recognizedGestures.clear();

                FingerDataLine fdl = (FingerDataLine) dl;
                // TODO
                // if (dl instanceof FingerDataLine) {} else if (dl instanceof WristDataLine) {}
                log.info("handComparators: " + handComparators.toString());
                for (Iterator<HandComparator> hci = handComparators.iterator(); hci.hasNext(); ) {
                    HandComparator hc = hci.next();
                    MultiSensorGestureMatcher matches = hc.compare(fdl);
                    if (matches != null && !matches.isEmpty()) {
                        log.info("Found gesture match1 at: " + matches.toString());

                        // TODO lets remove unnecessary data (quickfix for GestureCollectors  )
                        // Relies on RecognizerWSEndpoint:".*JsonInclude.Include.NON_NULL.*"
                        for(Map.Entry<Sensor, SingleSensorGestureMatcher> entry : matches.getSssgmm().entrySet()) {
                            entry.getValue().clearG();
                        }
                        matches.getG().setData(Collections.emptyList());
                        recognizedGestures.add(matches);
                    }
                }
                log.info("RecognizedGestures: " + recognizedGestures.toString());

                return recognizedGestures;
            }
        }
        return Collections.emptyList();
    }

}
