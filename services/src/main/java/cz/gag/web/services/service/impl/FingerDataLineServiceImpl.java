package cz.gag.web.services.service.impl;

import cz.gag.web.services.service.generic.GenericCRUDServiceImpl;
import cz.gag.web.services.service.FingerDataLineService;
import cz.gag.web.persistence.dao.FingerDataLineDao;
import cz.gag.web.persistence.entity.FingerDataLine;

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
