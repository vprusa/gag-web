package cz.muni.fi.gag.web.services.recognition.comparators;

import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vojtech Prusa
 */
public class SensorGestureR<T extends FingerDataLine> extends GestureR<T> {

    protected final Sensor s;

    public SensorGestureR(final Sensor s, final Gesture gRef) {
        super(gRef);
        this.s = s;
        this.ref = this.filterDataLines(gRef.getData());
        this.first = (T) this.ref.get(0);
    }

    Sensor getSensor() {
        return s;
    }

    @Override
    protected List<T> filterDataLines(List<T> dll) {
        return dll.stream().filter(dl -> dl.getPosition().equals(getSensor())).collect(Collectors.toList());
    }
}
    