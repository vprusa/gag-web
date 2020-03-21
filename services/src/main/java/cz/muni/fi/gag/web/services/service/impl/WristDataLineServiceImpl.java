package cz.muni.fi.gag.web.services.service.impl;

import cz.muni.fi.gag.web.persistence.entity.WristDataLine;
import cz.muni.fi.gag.web.services.service.generic.GenericCRUDServiceImpl;
import cz.muni.fi.gag.web.services.service.WristDataLineService;
import cz.muni.fi.gag.web.persistence.dao.WristDataLineDao;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Vojtech Prusa
 *
 */
@Stateless
public class WristDataLineServiceImpl extends GenericCRUDServiceImpl<WristDataLine, WristDataLineDao>
        implements WristDataLineService {

    @Inject
    protected WristDataLineDao genericDao;

    @Override
    public WristDataLineDao getDao() {
        return genericDao;
    }

}
