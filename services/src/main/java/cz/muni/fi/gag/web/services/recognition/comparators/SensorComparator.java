package cz.muni.fi.gag.web.services.recognition.comparators;

import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Vojtech Prusa
 */
public class SensorComparator<T extends FingerDataLine> extends BaseComparator<T> {

    protected final Sensor s;

    public SensorComparator(final Sensor s, final Gesture gRef, final DataLineGestureIterator dlgIter) {
        super(gRef, dlgIter);
        this.s = s;
        if (dlgIter.hasNext()) {
            this.first = getDL(0);
            this.refList.add(this.first);
        }
    }

    Sensor getSensor() {
        return s;
    }

    @Override
    protected List<T> filterDataLines(List<T> dll) {
        return dll.stream().filter(dl -> dl.getPosition().equals(getSensor())).collect(Collectors.toList());
    }

    @Override
    protected Stream<T> filterDataLines(Stream<T> dls) {
        return dls.filter(dl -> dl.getPosition().equals(getSensor()));
    }

    @Override
    protected T getDL(int index) {
        T dl = null;
        while (getRefSize() < index) {
            if (this.dlgIter.hasNext()) {
                dl = (T) this.dlgIter.next();
                if(this.s.equals(dl.getPosition())){
                    refList.add(dl);
                }
            }

        }
        return dl;
    }
}
    