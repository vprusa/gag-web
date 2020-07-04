package cz.muni.fi.gag.web.services.recognition.comparators;

import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;

/**
 * @author Vojtech Prusa
 *
 * {@link HandComparator}
 */
public class FingerComparator extends SensorComparator<FingerDataLine> {
    public FingerComparator(Sensor s, Gesture gRef, DataLineGestureIterator dlgIter) {
        super(s, gRef, dlgIter);
    }
}
    