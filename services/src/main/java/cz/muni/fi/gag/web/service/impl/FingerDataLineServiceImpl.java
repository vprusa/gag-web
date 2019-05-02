package cz.muni.fi.gag.web.service.impl;

import cz.muni.fi.gag.web.service.FingerDataLineService;
import cz.muni.fi.gag.web.service.generic.GenericCRUDServiceImpl;
import cz.muni.fi.gag.web.dao.FingerDataLineDao;
import cz.muni.fi.gag.web.entity.FingerDataLine;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Vojtech Prusa
 *
 */
@Stateless
public class FingerDataLineServiceImpl extends GenericCRUDServiceImpl<FingerDataLine, FingerDataLineDao>
        implements FingerDataLineService {

    @Inject
    protected FingerDataLineDao genericDao;

    @Override
    public FingerDataLineDao getDao() {
        return genericDao;
    }

}
