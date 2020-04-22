package cz.muni.fi.gag.web.services.service.impl;

import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.muni.fi.gag.web.services.service.generic.GenericCRUDServiceImpl;
import cz.muni.fi.gag.web.persistence.dao.DataLineDao;
import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.services.service.DataLineService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;

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
        List<DataLine> dll = dao.findByGestureId(gestureId);
        return dll;
    }

    // TODO it seems wrong to instantiate iterator...? or should it be by
    // implementing Iterable? No more wrappers?
    @Override
    public Stream<DataLine> getStream(long gestureId) {
        return getDao().getStream(gestureId);
    }

    @Override
    public int removeBy(Long gestureId) {
        return getDao().removeBy(gestureId);
    }

    @Override
    public List<DataLine> getInteresting(Long gestureId) {
        return getDao().getInteresting(gestureId);
    }

    // TODO it seems wrong to instantiate iterator...? or should it be by
    // implementing Iterable? No more wrappers?
    @Override
    public DataLineGestureIterator buildIteratorByGesture(long gestureId) {
        return new DataLineGestureIterator(getDao(), gestureId);
    }

}
