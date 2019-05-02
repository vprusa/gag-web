package cz.muni.fi.gag.web.service.impl;

import cz.muni.fi.gag.web.service.WristDataLineService;
import cz.muni.fi.gag.web.service.generic.GenericCRUDServiceImpl;
import cz.muni.fi.gag.web.dao.WristDataLineDao;
import cz.muni.fi.gag.web.entity.WristDataLine;

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
