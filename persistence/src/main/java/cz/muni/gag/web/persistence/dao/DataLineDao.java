package cz.gag.web.persistence.dao;

import cz.gag.web.persistence.dao.impl.DataLineDaoImpl;
import cz.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.gag.web.persistence.entity.DataLine;
import cz.gag.web.persistence.entity.Sensor;

import java.util.List;
import java.util.stream.Stream;

/**
 * 
 * @author Miloslav Zezulka, Vojtech Prusa
 *
 * {@link DataLineDaoImpl}
 * {@link DataLineGestureIterator}
 *
 *
 */
public interface DataLineDao extends GenericDao<DataLine> {

    List<DataLine> findByGestureId(long gestureId);

    Stream<DataLine> getStream(long gestureId);

    List<DataLine> getChunkForGesture(long gestureId, int offset, int limit);
    List<DataLine> getChunkForGesture(long gestureId, int offset, int limit, Sensor s);

    int removeBy(Long gestureId);

    List<DataLine> getInteresting(Long gestureId);

    long getSize(long gestureId, Sensor s);
    long getSize(long gestureId);

}
