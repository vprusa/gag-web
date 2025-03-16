package cz.gag.web.services.service.impl;

import cz.gag.web.services.service.generic.GenericCRUDServiceImpl;
import cz.gag.web.services.service.SensorOffsetService;
import cz.gag.web.persistence.dao.SensorOffsetDao;
import cz.gag.web.persistence.entity.SensorOffset;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Vojtech Prusa
 *
 */
@Stateless
public class SensorOffsetServiceImpl extends GenericCRUDServiceImpl<SensorOffset, SensorOffsetDao>
        implements SensorOffsetService {

    @Inject
    protected SensorOffsetDao genericDao;

    @Override
    public SensorOffsetDao getDao() {
        return genericDao;
    }

    @Override
    public List<SensorOffset> findByOffsetsAndPosition(
            Long handDeviceId, Long position, Long sensorType
    ) {
        return genericDao.findByOffsetsAndPosition(handDeviceId, position, sensorType);
    }
}
