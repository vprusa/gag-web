package cz.muni.fi.gag.web.services.websocket.service;

import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;
import cz.muni.fi.gag.web.services.logging.Log;
import cz.muni.fi.gag.web.services.recognition.GestureMatcher;
import cz.muni.fi.gag.web.services.recognition.comparators.SensorComparator;
import cz.muni.fi.gag.web.services.service.DataLineService;
import org.jboss.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

import static cz.muni.fi.gag.web.services.websocket.service.GestureRecognizer.RecognizerState.IDLE;
import static cz.muni.fi.gag.web.services.websocket.service.GestureRecognizer.RecognizerState.RECOGNIZING;

/**
 * @author Vojtech Prusa
 */
@Named
//@ApplicationScoped
@SessionScoped
//public class GestureRecognizer implements Runnable, Serializable {
public class GestureRecognizer implements Serializable {

    public static final Logger log = Logger.getLogger(GestureRecognizer.class.getSimpleName());

    private RecognizerState state = IDLE;

    @Inject
    private DataLineService dataLineService;

    @Inject
    private Gesture gestureService;

    public boolean isRecognize() {
        return state == RECOGNIZING;
    }

    // preparing for further states
    public enum RecognizerState {IDLE, RECOGNIZING}

    public RecognizerState getState() {
        synchronized (state) {
            return state;
        }
    }

    public void start() {
        synchronized (state) {
            this.state = RECOGNIZING;
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
            case IDLE: {}
            break;
            case RECOGNIZING: {
                List<GestureMatcher> recognizedGestures = new ArrayList<GestureMatcher>();

                List<Optional<Gesture>> lGOpts = gestureService.findActive();

                Iterator<Optional<Gesture>> git = lGOpts.iterator();
                while (git.hasNext()) {
                    Optional<Gesture> gOpt = git.next();
                    if (gOpt.isPresent()) {
                        Gesture gRef = gOpt.get();

                        // for any sensor it should behave as  SensorComparator<FingerDataLine>
                        SensorComparator sgi = new SensorComparator<FingerDataLine>(Sensor.INDEX, gRef);
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
                }
                return recognizedGestures;
            }
        }
        return Collections.emptyList();
    }

}
