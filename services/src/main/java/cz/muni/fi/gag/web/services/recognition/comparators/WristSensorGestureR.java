package cz.muni.fi.gag.web.services.recognition.comparators;

import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;
import cz.muni.fi.gag.web.persistence.entity.WristDataLine;

/**
 * @author Vojtech Prusa
 */
public class WristSensorGestureR extends SensorGestureR<WristDataLine> {
    public WristSensorGestureR(Sensor s, Gesture gRef) {
        super(s, gRef);
    }
}
    