package cz.gag.web.services.service;

import cz.gag.web.persistence.dao.DataLineDao;
import cz.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.gag.web.persistence.dao.impl.DataLineGestureSensorIterator;
import cz.gag.web.persistence.entity.DataLine;
import cz.gag.web.persistence.entity.Sensor;
import cz.gag.web.services.service.generic.GenericCRUDService;
import cz.gag.web.services.service.impl.DataLineServiceImpl;
import cz.gag.web.services.service.impl.FingerDataLineServiceImpl;
import cz.gag.web.services.service.impl.WristDataLineServiceImpl;

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

    DataLineGestureSensorIterator buildIterator(long gestureId, Sensor s);
    DataLineGestureIterator buildIterator(long gestureId);

    Stream<DataLine> getStream(long gestureId);

    int removeBy(Long gestureId);

    List<DataLine> getInteresting(Long gestureId);

}
