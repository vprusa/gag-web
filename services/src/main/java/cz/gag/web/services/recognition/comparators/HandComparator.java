package cz.gag.web.services.recognition.comparators;

import cz.gag.web.persistence.dao.impl.DataLineGestureSensorIterator;
import cz.gag.web.persistence.entity.FingerDataLine;
import cz.gag.web.persistence.entity.Gesture;
import cz.gag.web.persistence.entity.Sensor;
import cz.gag.web.services.logging.Log;
import cz.gag.web.services.recognition.GestureCollector;
import cz.gag.web.services.recognition.matchers.MultiSensorGestureMatcher;
import cz.gag.web.services.recognition.matchers.SingleSensorGestureMatcher;

import java.util.List;

/**
 * @author Vojtech Prusa
 */
//public class HandComparator implements GestureMatchComparator<FingerDataLine> {
public class HandComparator {

    public static final Log.TypedLogger log = new Log.TypedLogger<Log.LoggerTypeWSRecognizerComparator>(Log.LoggerTypeWSRecognizerComparator.class);

    public final SensorComparator[] fingers = new SensorComparator[Sensor.values().length];

    // TODO rename, refactor this
    // purpose: to be able to distinguish between singe gestures of whole hand, few sensors or 1 sensor
    // TODO use this as persistent/input variable from DB?
    // TODO in case of many sensors
    // a) byte -> short|int|... or byte -> bytes
    // b) split this class

    public GestureCollector gmlRet;

    // TODO refactor this data bottleneck for 'DataLineGestureSensorIterator[] dlgsIters' is ugly
    // TODO rename Comparator to GestureCollector or HandGestureCollector or AllSensorRecognizedGestureCollector?
    public HandComparator(Gesture gRef, DataLineGestureSensorIterator[] dlgsIters) {
        gmlRet = new GestureCollector(gRef);

        for (Sensor s : Sensor.values()) {
            if (gmlRet.doesGestureContainsSensor(s)) {
                if (s == Sensor.WRIST) {
                    fingers[s.ordinal()] = new WristComparator(s, gRef, dlgsIters[s.ordinal()]);
                } else {
                    fingers[s.ordinal()] = new FingerComparator(s, gRef, dlgsIters[s.ordinal()]);
                }
            }
        }
    }

    public MultiSensorGestureMatcher compare(FingerDataLine fdl) {
        if (!gmlRet.doesGestureContainsSensor(fdl.getPosition())) {
            return null;
        }

        Sensor pos = fdl.getPosition();
        List<SingleSensorGestureMatcher> gmlPos = fingers[pos.ordinal()].compare(fdl);
        log.info("HandComparator.compare: comparing " + gmlPos.toString());

        return gmlRet.collect(gmlPos);
    }

}
    