package cz.muni.fi.gag.web.services.websocket.service;

import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureSensorIterator;
import cz.muni.fi.gag.web.persistence.entity.*;
import cz.muni.fi.gag.web.services.logging.Log;
import cz.muni.fi.gag.web.services.recognition.GestureMatcher;
import cz.muni.fi.gag.web.services.recognition.comparators.HandComparator;
import cz.muni.fi.gag.web.services.service.DataLineService;
import cz.muni.fi.gag.web.services.service.GestureService;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

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

    public static final Logger log = Logger.getLogger(GestureRecognizer.class.getSimpleName());

    private RecognizerState state = IDLE;

    @Inject
    private DataLineService dataLineService;

    @Inject
    private GestureService gestureService;

//    @Inject
//    private AuthenticationService authService;

    // TODO fix re-init with start()
    protected List<GestureMatcher> recognizedGestures = new ArrayList<GestureMatcher>();
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
            recognizedGestures = new ArrayList<GestureMatcher>();
            log.info("for current: " + current.toString());
            List<Gesture> lGOpts = gestureService.findActive(current);
            gesturesIters = new HashMap<Gesture, DataLineGestureSensorIterator[]>();
            log.info("for lGOpts: " + lGOpts.toString());

            Iterator<Gesture> git = lGOpts.iterator();
            while (git.hasNext()) {
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

    public List<GestureMatcher> recognize(DataLine dl) {
        Log.info("Recognize: " + dl.toString());
        switch (getState()) {
            case IDLE: {
            }
            break;
            case RECOGNIZING: {
                log.info("case: RECOGNIZING");
                Iterator<Gesture> git = gesturesIters.keySet().iterator();
                while (git.hasNext()) {
                    Gesture gRef = git.next();
                    log.info("recognize for gesture: " + gRef.toString());
                    DataLineGestureSensorIterator[] dlgIter = gesturesIters.get(gRef);
                    // for any sensor it should behave as  SensorComparator<FingerDataLine>
//                    SensorComparator sgi = new SensorComparator<FingerDataLine>(Sensor.INDEX, gRef, dlgIter);
                    HandComparator sgi = new HandComparator(gRef, dlgIter);
                    GestureMatcher match = null;

//                        if (dl instanceof FingerDataLine) {
                    FingerDataLine fdl = (FingerDataLine) dl;
                    match = sgi.compare(fdl);
                    if (match != null) {
                        log.info("Found gesture match at: " + match);
                        recognizedGestures.add(match);
                        break;
                    }
//                        } else if (dl instanceof WristDataLine) {
//                        }
                }
                return recognizedGestures;
            }
        }
        return Collections.emptyList();
    }

}
