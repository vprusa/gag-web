package cz.muni.fi.gag.web.service.impl;

import cz.muni.fi.gag.web.service.SensorOffsetService;
import cz.muni.fi.gag.web.service.generic.GenericCRUDServiceImpl;
import cz.muni.fi.gag.web.dao.SensorOffsetDao;
import cz.muni.fi.gag.web.entity.SensorOffset;

import javax.ejb.Stateless;
import javax.inject.Inject;

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

}
