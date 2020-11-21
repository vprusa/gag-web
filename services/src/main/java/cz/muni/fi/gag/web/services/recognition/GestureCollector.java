package cz.muni.fi.gag.web.services.recognition;

import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.Sensor;
import cz.muni.fi.gag.web.services.logging.Log;
import cz.muni.fi.gag.web.services.recognition.matchers.MultiSensorGestureMatcher;
import cz.muni.fi.gag.web.services.recognition.matchers.SingleSensorGestureMatcher;

import java.util.Iterator;
import java.util.List;

/**
 * @author Vojtech Prusa
 *
 * This class collects gestures from sliding window across time
 *
 */
public class GestureCollector {

    public static final Log.TypedLogger log = new Log.TypedLogger<Log.LoggerTypeWSRecognizerComparator>(Log.LoggerTypeWSRecognizerComparator.class);

    // just convenient way to access 1
    private static final byte one = 0x01;
    public final byte gestureContainsSensors;
    public byte gestureContainsSensorsRet = 0x00;

    private MultiSensorGestureMatcher mgm;

    public GestureCollector(Gesture gRef){
        mgm = new MultiSensorGestureMatcher(gRef);
        byte gestureContainsSensorsTmp = 0;
        {
            byte i = 0;
            // TODO !!! Load data using custom iterator!
            // TODO improve `if(i >= Sensor.values().length)` its not sufficient and may some day broke..
            // TODO fix lazy loading
            //  (its not lazy and fetch left solutions results in
            //  https://issues.redhat.com/browse/WFLY-6696 )

            for (Iterator<DataLine> dli = gRef.getData().iterator(); dli.hasNext(); i++) {
                Sensor s = dli.next().getPosition();
                gestureContainsSensorsTmp = (byte) (gestureContainsSensorsTmp | (one << s.ordinal()));
                if (i >= Sensor.values().length) {
                    // this block relies on condition that
                    // gRef contains filtered data that are ordered by n-ths (fingers.length)
                    // thus first n (fingers.length) records contains all used sensor positions
                    // this is hardcoded performance optimization ...
                    break;
                }
            }
        }
        this.gestureContainsSensors = gestureContainsSensorsTmp;
    }

    public boolean doesGestureContainsSensor(Sensor s) {
        return ((this.gestureContainsSensors >> s.ordinal()) & one) == one;
    }

    public MultiSensorGestureMatcher collect(List<SingleSensorGestureMatcher> gmlPos) {
        // TODO rework and reconsider the data loss that may occur here
        // - List<GestureMatcher> is converted to GestureMatcher
        // -- sometimes it return List<GestureMatcher> of sensor data
        // -- other times it returns (GestureMatcher[] -> List<GestureMatcher>) of gesture match for each sensor on hand
        // so far i do not really care ... i will care when i will split whole hand gesture recognition workflow to single (or multiple) sensor gestures
        // then I will have to rework
        // - return List<GestureMatcher>
        // - gm.setSingle(true);
        // -- because of distinguishing between single sensor, multiple sensor or whole hand gesture

        if(gestureContainsSensorsRet == 0x00 && !mgm.isEmpty()) {
//            log.info("HandComparator.collect.mgm.clear()");
//            log.info("mgm 1. " + mgm.toString());
//            mgm.clear();
        }
        for (Iterator<SingleSensorGestureMatcher> gmi = gmlPos.iterator(); gmi.hasNext(); ) {
            SingleSensorGestureMatcher gm = gmi.next();
            Sensor posP = gm.getAtDataLine().getPosition();
            if(!mgm.containsValue(posP)) {
                gestureContainsSensorsRet = (byte) (gestureContainsSensorsRet | (one << gm.getAtDataLine().getPosition().ordinal()));
                mgm.put(posP,gm);
            }
            log.info("HandComparator.gmlRet");
            log.info(toString());
            if(gestureContainsSensors == gestureContainsSensorsRet) {
                gestureContainsSensorsRet = 0x00;
                return mgm;
            }
        }
        return null;
//        return Collections.emptyMap();
    }

}
