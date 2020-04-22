package cz.muni.fi.gag.web.services.service;

import cz.muni.fi.gag.web.persistence.dao.DataLineDao;
import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.services.service.generic.GenericCRUDService;
import cz.muni.fi.gag.web.services.service.impl.DataLineServiceImpl;
import cz.muni.fi.gag.web.services.service.impl.FingerDataLineServiceImpl;
import cz.muni.fi.gag.web.services.service.impl.WristDataLineServiceImpl;

import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Vojtech Prusa
 *
 * {@link DataLineServiceImpl}
 * {@link FingerDataLineServiceImpl}
 * {@link WristDataLineServiceImpl}
 */
public interface DataLineService extends GenericCRUDService<DataLine, DataLineDao> {

    List<DataLine> findByGestureId(long gestureId);

    DataLineGestureIterator buildIteratorByGesture(long gestureId);

    Stream<DataLine> getStream(long gestureId);

    int removeBy(Long gestureId);

    List<DataLine> getInteresting(Long gestureId);

}
