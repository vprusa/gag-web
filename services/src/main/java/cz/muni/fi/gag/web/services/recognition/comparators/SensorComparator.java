package cz.muni.fi.gag.web.services.recognition.comparators;

import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;
import cz.muni.fi.gag.web.services.logging.Log;

/**
 * @author Vojtech Prusa
 * {@link HandComparator}
 * {@link FingerComparator}
 * {@link WristComparator}
 */
public class SensorComparator<T extends FingerDataLine> extends BaseComparator<T> {

    public static final Log.TypedLogger log = new Log.TypedLogger<Log.LoggerTypeWSRecognizerComparator>(Log.LoggerTypeWSRecognizerComparator.class);

    protected final Sensor s;

    public SensorComparator(final Sensor s, final Gesture gRef, final DataLineGestureIterator dlgIter) {
        super(gRef, dlgIter);
        this.s = s;
//        if (dlgIter.hasNext()) {
        this.first = getDL(0);
//        this.refList.add(this.first);
//        }
    }

    Sensor getSensor() {
        return s;
    }

    @Override
    protected T getDL(int index) {
        log.info("SensorComparator.getDL");
        log.info("SensorComparator.getDL getRefSize: " + getRefSize() + " index: " + index);

        T ret = null;
        if (refList.size() > index) {
            ret = refList.get(index);
        } else {
            while (getRefSize() <= index) {
                T dl = null;
                if (this.dlgIter.hasNext()) {
                    dl = (T) this.dlgIter.next();
                    log.info("SensorComparator.getDL dl " + dl);
                    if (this.s.equals(dl.getPosition())) {
                        refList.add(dl);
                        ret = dl;
                    }
                } else {
                    log.info("SensorComparator.getDL break; should not happen!");
                    // TODO return null;
                    // not found
                    break;
                }
            }
        }
        if (ret == null && getRefSize() > index) {
            ret = refList.get(index);
        }
        log.info("SensorComparator.getDL ret " + ret);
        return ret;
    }
}
    