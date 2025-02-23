package cz.muni.fi.gag.web.persistence.dao.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;

import cz.muni.fi.gag.web.persistence.dao.SensorOffsetDao;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.SensorOffset;

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
        TypedQuery<SensorOffset> q = em.createQuery("SELECT so FROM SensorOffset so " +
                        "WHERE device = :handDevice AND position = :position AND sensorType = :sensorType",
                        SensorOffset.class)
                .setParameter("handDevice", handDeviceId)
                .setParameter("position", position)
                .setParameter("sensorType", sensorType);
        List<SensorOffset> results = q.getResultList();
        return results;
    };
}
