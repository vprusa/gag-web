package cz.muni.fi.gag.web.common;

import cz.muni.fi.gag.web.dao.FingerSensorOffsetDao;
import cz.muni.fi.gag.web.dao.HandDeviceDao;
import cz.muni.fi.gag.web.dao.UserDao;
import cz.muni.fi.gag.web.entity.*;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Vojtech Prusa
 */
public class TestServiceBase extends TestBase {

    private static Logger log = Logger.getLogger(TestServiceBase.class.getSimpleName());

    @Inject
    private UserDao userDao;

    @Inject
    private HandDeviceDao handDeviceDao;

    @Inject
    private FingerSensorOffsetDao fingerSensorOffsetDao;

    public static int thirdPartyIdCounter = 0;
    public static int deviceIdCounter = 0;

    public User buildUser() {
        User r = new User();
        r.setThirdPartyId("rand" + (++thirdPartyIdCounter));
        r.setRole(UserRole.USER);
        return r;
    }

    public FingerDataLine buildFingerDataLine() {
        FingerDataLine r = new FingerDataLine();
        r.setGesture(null);
        r.setPosition(FingerPosition.INDEX);
        r.setQuatA(0);
        r.setQuatX(0);
        r.setQuatY(0);
        r.setQuatZ(0);
        r.setTimestamp(new Date());
        r.setAccX((short) 0);
        r.setAccY((short) 0);
        r.setAccZ((short) 0);
        return r;
    }

    public WristDataLine buildWristDataLine() {
        WristDataLine r = new WristDataLine();
        r.setGesture(null);
        r.setPosition(FingerPosition.INDEX);
        r.setQuatA(0);
        r.setQuatX(0);
        r.setQuatY(0);
        r.setQuatZ(0);
        r.setTimestamp(new Date());
        r.setAccX((short) 0);
        r.setAccY((short) 0);
        r.setAccZ((short) 0);
        r.setMagX((short) 0);
        r.setMagY((short) 0);
        r.setMagZ((short) 0);
        return r;
    }



    public DataLine buildDataLine() {
        DataLine r = new DataLine(){};
        r.setGesture(null);
        r.setTimestamp(new Date());
        return r;
    }

    public FingerSensorOffset buildFingerSensorOffset(FingerPosition fingerPosition) {
        FingerSensorOffset r = new FingerSensorOffset();
        HandDevice hd = null;
        r.setDevice(hd);
        r.setSensorType(SensorType.QUAT_GYRO);
        r.setX((short) 0);
        r.setY((short) 0);
        r.setZ((short) 0);
        r.setPosition(fingerPosition);
        return r;
    }

    public FingerSensorOffset buildFingerSensorOffsetWithPersistentRefs(FingerPosition fingerPosition) {
        FingerSensorOffset r = buildFingerSensorOffset(fingerPosition);
        HandDevice d = buildHandDeviceAndPersist();
        r.setDevice(d);
        // fingerSensorOffsetDao.create(r);
        return r;
    }

    public WristSensorOffset buildWristSensorOffset() {
        WristSensorOffset r = new WristSensorOffset();

        HandDevice hd = null;
        r.setDevice(hd);
        r.setSensorType(SensorType.QUAT_GYRO);
        r.setX((short) 0);
        r.setY((short) 0);
        r.setZ((short) 0);

        return r;
    }

    public WristSensorOffset buildWristSensorOffsetWithPersistentRefs() {
        WristSensorOffset r = buildWristSensorOffset();
        HandDevice d = buildHandDeviceAndPersist();
        r.setDevice(d);
        return r;
    }

    public HandDevice buildHandDeviceAndPersist() {
        HandDevice r = buildHandDevice();
        r = handDeviceDao.create(r);
        return r;
    }

    public HandDevice buildHandDevice() {
        User u = buildUser();
        u = userDao.create(u);

        List<SensorOffset> o = new ArrayList<>();
        for (FingerPosition fingerPosition : FingerPosition.values()) {
            o.add(buildFingerSensorOffset(fingerPosition));
        }

        o.add(buildWristSensorOffset());

        HandDevice r = new HandDevice();
        r.setDeviceId("devId" + (++deviceIdCounter));
        r.setUser(u);
        r.setOffsets(o);
        return r;
    }

}
