package cz.muni.fi.gag.web.services.recognition.comparators;

import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;
import cz.muni.fi.gag.web.services.recognition.GestureMatchComparator;
import cz.muni.fi.gag.web.services.recognition.GestureMatcher;

/**
 * @author Vojtech Prusa
 */
public class HandComparator implements GestureMatchComparator<FingerDataLine> {
    public final SensorComparator[] fingers = new SensorComparator[Sensor.values().length];

    public HandComparator(Gesture gRef, DataLineGestureIterator dlgIter) {
        for (int i = 0; i < fingers.length; i++) {
            Sensor s = Sensor.values()[i];
            if (s == Sensor.WRIST) {
                fingers[i] = new WristComparator(s, gRef, dlgIter);
            } else {
                fingers[i] = new FingerComparator(s, gRef, dlgIter);
            }
        }
    }

    public GestureMatcher compare(FingerDataLine fdl) {
        Sensor pos = fdl.getPosition();
        GestureMatcher gm = fingers[pos.ordinal()].compare(fdl);
        return gm;
    }
}
    