package cz.muni.fi.gag.web.persistence.dao;

import cz.muni.fi.gag.web.persistence.dao.impl.DataLineDaoImpl;
import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.muni.fi.gag.web.persistence.entity.DataLine;

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

    int removeBy(Long gestureId);

    List<DataLine> getInteresting(Long gestureId);
}
