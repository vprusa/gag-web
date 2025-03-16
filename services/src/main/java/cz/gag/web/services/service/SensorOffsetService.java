package cz.gag.web.services.service;

import cz.gag.web.services.service.generic.GenericCRUDService;
import cz.gag.web.persistence.dao.SensorOffsetDao;
import cz.gag.web.persistence.entity.SensorOffset;

import java.util.List;

/**
 *
 * @author Vojtech Prusa
 *
 * @SensorOffsetServiceImpl
 *
 */
public interface SensorOffsetService extends GenericCRUDService<SensorOffset, SensorOffsetDao> {

    List<SensorOffset> findByOffsetsAndPosition(Long handDeviceId, Long position, Long sensorType);
}
