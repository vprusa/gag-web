package cz.muni.fi.gag.web.services.recognition.comparators;

import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;
import cz.muni.fi.gag.web.services.recognition.GestureComparator;
import cz.muni.fi.gag.web.services.recognition.GestureMatcher;

/**
 * @author Vojtech Prusa
 */
public class HandSensorGestureR implements GestureComparator<FingerDataLine> {
    public final SensorGestureR[] fingers = new SensorGestureR[Sensor.values().length];

    public HandSensorGestureR(Gesture gRef) {
        for (int i = 0; i < fingers.length; i++) {
            Sensor s = Sensor.values()[i];
            if (s == Sensor.WRIST) {
                fingers[i] = new WristSensorGestureR(s, gRef);
            } else {
                fingers[i] = new FingerSensorGestureR(s, gRef);
            }
        }
    }

    public GestureMatcher compare(FingerDataLine fdl) {
        Sensor pos = fdl.getPosition();
        GestureMatcher gm = fingers[pos.ordinal()].compare(fdl);
        return gm;
    }
}
    