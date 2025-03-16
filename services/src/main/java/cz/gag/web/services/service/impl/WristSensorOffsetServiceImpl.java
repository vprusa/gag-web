package cz.gag.web.services.service.impl;

import cz.gag.web.services.service.generic.GenericCRUDServiceImpl;
import cz.gag.web.services.service.WristSensorOffsetService;
import cz.gag.web.persistence.dao.WristSensorOffsetDao;
import cz.gag.web.persistence.entity.WristSensorOffset;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Vojtech Prusa
 *
 */
@Stateless
public class WristSensorOffsetServiceImpl extends GenericCRUDServiceImpl<WristSensorOffset, WristSensorOffsetDao>
        implements WristSensorOffsetService {

    @Inject
    protected WristSensorOffsetDao genericDao;

    @Override
    public WristSensorOffsetDao getDao() {
        return genericDao;
    }

}
