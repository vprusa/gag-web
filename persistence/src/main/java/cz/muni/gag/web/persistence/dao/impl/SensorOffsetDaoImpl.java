package cz.gag.web.persistence.dao.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;

import cz.gag.web.persistence.dao.SensorOffsetDao;
import cz.gag.web.persistence.entity.Gesture;
import cz.gag.web.persistence.entity.SensorOffset;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class SensorOffsetDaoImpl extends AbstractGenericDao<SensorOffset> implements SensorOffsetDao, Serializable {

    public SensorOffsetDaoImpl() {
        super(SensorOffset.class);
    }

    public List<SensorOffset> findByOffsetsAndPosition(
            Long handDeviceId, Long position, Long sensorType
    ) {
        /*
        TypedQuery<SensorOffset> q = em.createQuery("SELECT so FROM SensorOffset so " +
                        "WHERE device = :handDevice AND position = :position AND sensorType = :sensorType",
                        SensorOffset.class)
                .setParameter("handDevice", handDeviceId)
                .setParameter("position", position)
                .setParameter("sensorType", sensorType);

        List<SensorOffset> results = q.getResultList();
        return results;
        */
        /*
        return em.createNativeQuery(
                    "SELECT * FROM SensorOffset WHERE offsets = :handDeviceId AND position = :position AND sensorType = :sensorType",
                    SensorOffset.class)
            .setParameter("handDeviceId", handDeviceId)
            .setParameter("position", position)
            .setParameter("sensorType", sensorType)
            .getResultList();
        */
        TypedQuery<SensorOffset> q = em.createQuery("SELECT so FROM SensorOffset so " +
                                "WHERE so.device.id = :handDeviceId AND so.position = :position AND so.sensorType = :sensorType",
                        SensorOffset.class)
                .setParameter("handDeviceId", handDeviceId)
                .setParameter("position", cz.gag.web.persistence.entity.Sensor.values()[position.intValue()])
                .setParameter("sensorType", cz.gag.web.persistence.entity.SensorType.values()[sensorType.intValue()]);

        List<SensorOffset> results = q.getResultList();
        return results;

    };
}
