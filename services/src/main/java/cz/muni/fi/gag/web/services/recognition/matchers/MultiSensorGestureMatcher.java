package cz.muni.fi.gag.web.services.recognition.matchers;

import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @author Vojtech Prusa
 */
public class MultiSensorGestureMatcher extends HashMap<Sensor, SingleSensorGestureMatcher> implements GestureMatcher{

    private final Gesture g;

    public MultiSensorGestureMatcher(Gesture g) {
        this.g = g;
    }

    @Override
    public String toString() {
        return "MultiSensorGestureMatcher{" +
//                "index=" + index +
                ", g.id=" + getG().getId() +
                ", g.userAlias=" + getG().getUserAlias() +
                ", DataLines: ( " + values().stream().map(SingleSensorGestureMatcher::toMinString)
                .collect(Collectors.joining(",")) + " )" +
//                ", atDataLine=" + atDataLine +
//                ", single=" + single +

                '}';
    }

    @Override
    public Gesture getG() {
        return g;
    }
}
