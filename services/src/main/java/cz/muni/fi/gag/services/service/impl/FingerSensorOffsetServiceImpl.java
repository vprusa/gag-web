package cz.muni.fi.gag.services.service.impl;

import cz.muni.fi.gag.services.service.FingerSensorOffsetService;
import cz.muni.fi.gag.services.service.generic.GenericCRUDServiceImpl;
import cz.muni.fi.gag.web.dao.FingerSensorOffsetDao;
import cz.muni.fi.gag.web.entity.FingerSensorOffset;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Vojtech Prusa
 *
 */
@Stateless
public class FingerSensorOffsetServiceImpl extends GenericCRUDServiceImpl<FingerSensorOffset, FingerSensorOffsetDao>
        implements FingerSensorOffsetService {

    @Inject
    protected FingerSensorOffsetDao genericDao;

    @Override
    public FingerSensorOffsetDao getDao() {
        return genericDao;
    }

}
