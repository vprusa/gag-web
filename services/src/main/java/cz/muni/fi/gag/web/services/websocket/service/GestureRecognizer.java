package cz.muni.fi.gag.web.services.websocket.service;

import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureSensorIterator;
import cz.muni.fi.gag.web.persistence.entity.*;
import cz.muni.fi.gag.web.services.logging.Log;
import cz.muni.fi.gag.web.services.recognition.comparators.HandComparator;
import cz.muni.fi.gag.web.services.recognition.matchers.GestureMatcher;
import cz.muni.fi.gag.web.services.recognition.matchers.MultiSensorGestureMatcher;
import cz.muni.fi.gag.web.services.service.DataLineService;
import cz.muni.fi.gag.web.services.service.GestureService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static cz.muni.fi.gag.web.services.websocket.service.GestureRecognizer.RecognizerState.IDLE;
import static cz.muni.fi.gag.web.services.websocket.service.GestureRecognizer.RecognizerState.RECOGNIZING;

/**
 * @author Vojtech Prusa
 * <p>
 * TODO for bean type change take a look at TODO
 * {@link DataLineRePlayer}
 */
@Named
@ApplicationScoped
//@Singleton
public class GestureRecognizer implements Serializable {

    //    public static final Logger log = Logger.getLogger(GestureRecognizer.class.getSimpleName());
    public static final Log.TypedLogger log = new Log.TypedLogger<Log.LoggerTypeWSRecognizer>(Log.LoggerTypeWSRecognizer.class);

    private RecognizerState state = IDLE;

    @Inject
    private DataLineService dataLineService;

    @Inject
    private GestureService gestureService;

//    HandComparator sgi = new HandComparator(gRef, dlgIter);
//    List<HandComparator> hcl;
    private HandComparator hc;


//    @Inject
//    private AuthenticationService authService;

    // TODO fix re-init with start()
    protected List<MultiSensorGestureMatcher> recognizedGestures = new ArrayList<MultiSensorGestureMatcher>();
    protected Map<Gesture, DataLineGestureSensorIterator[]> gesturesIters = new HashMap<Gesture, DataLineGestureSensorIterator[]>();

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
            recognizedGestures = new ArrayList<MultiSensorGestureMatcher>();
            log.info("for current: " + current.toString());
            List<Gesture> lGOpts = gestureService.findActive(current);
            gesturesIters = new HashMap<Gesture, DataLineGestureSensorIterator[]>();
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
                // TODO add
                gesturesIters.put(g, dlgsIters);
            }
            log.info("gesturesIters: " + gesturesIters.toString());
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
            case IDLE: {}
            break;
            case RECOGNIZING: {
                log.info("case: RECOGNIZING");

                for (Iterator<Gesture> git = gesturesIters.keySet().iterator(); git.hasNext(); ) {
                    Gesture gRef = git.next();
                    log.info("recognizing for gesture: " + gRef.toString());
                    DataLineGestureSensorIterator[] dlgIter = gesturesIters.get(gRef);
                    // for any sensor it should behave as  SensorComparator<FingerDataLine>
//                    SensorComparator sgi = new SensorComparator<FingerDataLine>(Sensor.INDEX, gRef, dlgIter);
                    // why is here new variable?

                    if(hc == null){
//                        hcl = new ArrayList<HandComparator>();
//                        HandComparator hc = new HandComparator(gRef, dlgIter);
                        // TODO inject bean on new session ?
                        hc = new HandComparator(gRef, dlgIter);
                    }

//                        if (dl instanceof FingerDataLine) {
                    FingerDataLine fdl = (FingerDataLine) dl;
                    List<MultiSensorGestureMatcher> matches = hc.compare(fdl);
                    log.info("Found gesture match1 at: " + matches.toString());
                    if (matches != null && !matches.isEmpty()) {
//                        log.info("Found gesture match at: " + matches);

                        try {
                            log.info("Found gesture match2 at: " + matches.stream().map(GestureMatcher::toString)
                                    .collect(Collectors.joining(";;;")));
                            log.info("Found gesture match3 at groupingBy: " + matches.stream().map(GestureMatcher::getG)
                                    .collect(Collectors.groupingBy(Gesture::getId)).toString());
                        } catch (Exception e) {
//                            log.info(e.stac);
                            e.printStackTrace();
                        }

                        recognizedGestures.addAll(matches);
//                         if it is needed to find shortest gesture match then: break; else keep running
//                        break;
                    }
//                    hcl.add(hc);
//                        } else if (dl instanceof WristDataLine) {
//                        }
                }
                log.info("RecognizedGestures: " + recognizedGestures.toString());

                return recognizedGestures;
            }
        }
        return Collections.emptyList();
    }

}
