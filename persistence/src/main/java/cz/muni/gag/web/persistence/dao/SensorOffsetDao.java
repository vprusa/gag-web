package cz.gag.web.persistence.dao;

import cz.gag.web.persistence.entity.SensorOffset;

import java.util.List;

/**
 * @author Vojtech Prusa
 *
 * @SensorOffsetDaoImpl
 */
public interface SensorOffsetDao extends GenericDao<SensorOffset> {
    public List<SensorOffset> findByOffsetsAndPosition(
            Long handDeviceId, Long position, Long sensorType
    );
}
