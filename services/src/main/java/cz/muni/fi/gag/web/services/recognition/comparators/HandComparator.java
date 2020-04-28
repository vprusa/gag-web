package cz.muni.fi.gag.web.services.recognition.comparators;

import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureSensorIterator;
import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;
import cz.muni.fi.gag.web.services.recognition.GestureMatchComparator;
import cz.muni.fi.gag.web.services.recognition.GestureMatcher;

import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 */
public class HandComparator implements GestureMatchComparator<FingerDataLine> {
    public static Logger log = Logger.getLogger(HandComparator.class.getSimpleName());

    public final SensorComparator[] fingers = new SensorComparator[Sensor.values().length];

    // TODO refactor this data bottleneck for 'DataLineGestureSensorIterator[] dlgsIters' is ugly
//    public HandComparator(Gesture gRef, DataLineService dataLineService) {
    public HandComparator(Gesture gRef, DataLineGestureSensorIterator[] dlgsIters) {
        for (int i = 0; i < fingers.length; i++) {
            Sensor s = Sensor.values()[i];
            if (s == Sensor.WRIST) {
//                fingers[i] = new WristComparator(s, gRef, dataLineService.buildIteratorByGesture(gRef.getId(), s));
                fingers[i] = new WristComparator(s, gRef, dlgsIters[i]);
            } else {
//                fingers[i] = new FingerComparator(s, gRef, dataLineService.buildIteratorByGesture(gRef.getId(), s));
                fingers[i] = new FingerComparator(s, gRef, dlgsIters[i]);
            }
        }
    }

    public GestureMatcher compare(FingerDataLine fdl) {
        Sensor pos = fdl.getPosition();
        GestureMatcher gm = fingers[pos.ordinal()].compare(fdl);
        return gm;
    }
}
    