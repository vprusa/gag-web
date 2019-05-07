package cz.muni.fi.gag.web.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import cz.muni.fi.gag.web.dao.FingerSensorOffsetDao;
import cz.muni.fi.gag.web.dao.HandDeviceDao;
import cz.muni.fi.gag.web.dao.UserDao;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.FingerPosition;
import cz.muni.fi.gag.web.entity.FingerSensorOffset;
import cz.muni.fi.gag.web.entity.HandDevice;
import cz.muni.fi.gag.web.entity.SensorOffset;
import cz.muni.fi.gag.web.entity.SensorType;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.web.entity.UserRole;
import cz.muni.fi.gag.web.entity.WristSensorOffset;

/**
 * @author Vojtech Prusa TODO ....
 */
public class TestBase {

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
        r.setType(UserRole.USER);
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
        r.setX((short) 0);
        r.setY((short) 0);
        r.setZ((short) 0);
        return r;
    }

    public FingerSensorOffset buildFingerSensorOffset() {
        FingerSensorOffset r = new FingerSensorOffset();
        HandDevice hd = null;
        r.setDevice(hd);
        r.setSensorType(SensorType.QUAT_GYRO);
        r.setX((short) 0);
        r.setY((short) 0);
        r.setZ((short) 0);
        r.setPosition(FingerPosition.INDEX);
        return r;
    }

    public FingerSensorOffset buildFingerSensorOffsetWithPersistentRefs() {
        FingerSensorOffset r = buildFingerSensorOffset();
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
        User u = buildUser();
        u = userDao.create(u);

        List<SensorOffset> o = Collections.emptyList();

        HandDevice r = new HandDevice();
        r.setDeviceId("devId" + (++deviceIdCounter));
        r.setUser(u);
        r.setOffsets(o);
        r = handDeviceDao.create(r);
        return r;
    }

}
