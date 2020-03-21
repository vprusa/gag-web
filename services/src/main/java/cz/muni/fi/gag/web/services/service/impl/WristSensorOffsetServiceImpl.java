package cz.muni.fi.gag.web.services.service.impl;

import cz.muni.fi.gag.web.services.service.generic.GenericCRUDServiceImpl;
import cz.muni.fi.gag.web.services.service.WristSensorOffsetService;
import cz.muni.fi.gag.web.persistence.dao.WristSensorOffsetDao;
import cz.muni.fi.gag.web.persistence.entity.WristSensorOffset;

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
