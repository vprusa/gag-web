package cz.muni.fi.gag.web.services.recognition.matchers;

import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @author Vojtech Prusa
 * <p>
 * HotFix
 * extends HashMap<Gesture,HashMap<Sensor, SingleSensorGestureMatcher>>
 * for easy getting parsing to JSON (TODO change from easy and stupid to using of Decoders)
 */
public class MultiSensorGestureMatcher
//        extends HashMap<Gesture,HashMap<Sensor, SingleSensorGestureMatcher>>
        extends HashMap<String,Object>
        implements GestureMatcher {

    public static final String GESTURE_MAP_KEY = "gest";
    public static final String DATA_MAP_KEY = "data";

    //    private HashMap<Sensor, SingleSensorGestureMatcher> ssgm;
    private final Gesture g;
    private HashMap<Sensor, SingleSensorGestureMatcher> sssgmm;

    public MultiSensorGestureMatcher(Gesture g) {
        this.g = g;
        this.sssgmm = new HashMap<Sensor, SingleSensorGestureMatcher>();
        this.put(GESTURE_MAP_KEY, g);
        this.put(DATA_MAP_KEY, sssgmm);
    }

    @Override
    public String toString() {
        return "MultiSensorGestureMatcher{" +
//                "index=" + index +
                ", g.id=" + getG().getId() +
                ", g.userAlias=" + getG().getUserAlias() +
                ", DataLines: ( " + sssgmm.values().stream().map(SingleSensorGestureMatcher::toMinString)
//                ", DataLines: ( " + this.get(g).values().stream().map(SingleSensorGestureMatcher::toMinString)
//                ", DataLines: ( " + this.get(DATA_MAP_KEY).values().stream().map(SingleSensorGestureMatcher::toMinString)
                .collect(Collectors.joining(",")) + " )" +
//                ", atDataLine=" + atDataLine +
//                ", single=" + single +
                '}';
    }

    @Override
    public Gesture getG() {
        return g;
    }

    public void put(Sensor s, SingleSensorGestureMatcher ssgm) {
//        this.get(g).put(s, ssgm);
        this.sssgmm.put(s,ssgm);
    }

    public HashMap<Sensor, SingleSensorGestureMatcher> getSssgmm() {
        return sssgmm;
    }

}
