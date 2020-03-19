package cz.muni.fi.gag.services.service;

import cz.muni.fi.gag.services.service.impl.DataLineServiceImpl;
import cz.muni.fi.gag.services.service.impl.FingerDataLineServiceImpl;
import cz.muni.fi.gag.services.service.impl.WristDataLineServiceImpl;
import cz.muni.fi.gag.web.dao.DataLineDao;
import cz.muni.fi.gag.web.dao.impl.DataLineGestureIterator;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.services.service.generic.GenericCRUDService;

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

    DataLineGestureIterator initIteratorByGesture(long gestureId);

    Stream<DataLine> getStream(long gestureId);

    int removeBy(Long gestureId);

    List<DataLine> getInteresting(Long gestureId);

}
