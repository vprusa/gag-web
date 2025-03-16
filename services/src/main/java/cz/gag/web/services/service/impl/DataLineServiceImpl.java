package cz.gag.web.services.service.impl;

import cz.gag.web.persistence.dao.DataLineDao;
import cz.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.gag.web.persistence.dao.impl.DataLineGestureSensorIterator;
import cz.gag.web.persistence.entity.DataLine;
import cz.gag.web.persistence.entity.Sensor;
import cz.gag.web.services.service.DataLineService;
import cz.gag.web.services.service.generic.GenericCRUDServiceImpl;

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
    // TODO return DataLineGestureIterator <-> SensorDataLineGestureIterator ... refactor to facade pattern
    // or remove unused DataLineGestureIterator ..?
    @Override
    public DataLineGestureSensorIterator buildIterator(long gestureId, Sensor s) {
        return new DataLineGestureSensorIterator(getDao(), gestureId, s);
    }

    @Override
    public DataLineGestureIterator buildIterator(long gestureId) {
        return new DataLineGestureIterator(getDao(), gestureId);
    }

}
