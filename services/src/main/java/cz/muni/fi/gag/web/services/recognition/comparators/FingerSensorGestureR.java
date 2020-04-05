package cz.muni.fi.gag.web.services.recognition.comparators;

import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;

/**
 * @author Vojtech Prusa
 */
public class FingerSensorGestureR extends SensorGestureR<FingerDataLine> {
    public FingerSensorGestureR(Sensor s, Gesture gRef) {
        super(s, gRef);
    }
}
    