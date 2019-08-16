package cz.muni.fi.gag.web.service.impl;

import cz.muni.fi.gag.web.service.DataLineService;
import cz.muni.fi.gag.web.service.generic.GenericCRUDServiceImpl;
import cz.muni.fi.gag.web.dao.DataLineDao;
import cz.muni.fi.gag.web.dao.impl.DataLineGestureIterator;
import cz.muni.fi.gag.web.entity.DataLine;

import java.util.Iterator;
import java.util.List;

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

    @Override
    public List<DataLine> findByGestureId(long gestureId) {
        DataLineDao dao = getDao();
        return dao.findByGestureId(gestureId);
    }

    // TODO it seems wrong to instantiate iterator...? or should it be by
    // implementing Iterable? No more wrappers?
    @Override
    public Iterator<DataLine> initIteratorByGesture(long gestureId) {
        return new DataLineGestureIterator(getDao(), gestureId);
    }

}
