package cz.muni.fi.gag.web.service.impl;

import cz.muni.fi.gag.web.service.DataLineService;
import cz.muni.fi.gag.web.service.generic.GenericCRUDServiceImpl;
import cz.muni.fi.gag.web.dao.DataLineDao;
import cz.muni.fi.gag.web.entity.DataLine;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Vojtech Prusa
 *
 */
@Stateless
public class DataLineServiceImpl extends GenericCRUDServiceImpl<DataLine, DataLineDao> implements DataLineService {

    @Inject
    protected DataLineDao genericDao;
    
    @Override
    public DataLineDao getDao() {
        return genericDao;
    }
}
