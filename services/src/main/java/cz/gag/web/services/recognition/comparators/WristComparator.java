package cz.gag.web.services.recognition.comparators;

import cz.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.gag.web.persistence.entity.Gesture;
import cz.gag.web.persistence.entity.Sensor;
import cz.gag.web.persistence.entity.WristDataLine;

/**
 * @author Vojtech Prusa
 */
public class WristComparator extends SensorComparator<WristDataLine> {
    public WristComparator(Sensor s, Gesture gRef, DataLineGestureIterator dlgIter) {
        super(s, gRef, dlgIter);
    }

    // in case of WristComparator this should be overriden
    @Override
    protected boolean doesMatch(final WristDataLine fdlRef, WristDataLine fdl) {
//        double dist = quatsAbsDist(qRef, q);
        return super.doesMatch(fdlRef,fdl) && doesMagMatch(fdlRef,fdl);
    }

    protected boolean doesMagMatch(final WristDataLine fdlRef, WristDataLine fdl) {
        // TODO
        return true;
    }

}
    