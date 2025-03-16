package cz.gag.web.services.service.impl;

import cz.gag.web.services.service.generic.GenericCRUDServiceImpl;
import cz.gag.web.services.service.HandDeviceService;
import cz.gag.web.persistence.dao.HandDeviceDao;
import cz.gag.web.persistence.entity.HandDevice;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Vojtech Prusa
 *
 */
@Stateless
public class HandDeviceServiceImpl extends GenericCRUDServiceImpl<HandDevice, HandDeviceDao>
        implements HandDeviceService {

    @Inject
    protected HandDeviceDao genericDao;

    @Override
    public HandDeviceDao getDao() {
        return genericDao;
    }

}
