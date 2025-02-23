package cz.muni.fi.gag.web.services.service;

import cz.muni.fi.gag.web.services.service.generic.GenericCRUDService;
import cz.muni.fi.gag.web.persistence.dao.SensorOffsetDao;
import cz.muni.fi.gag.web.persistence.entity.SensorOffset;

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
