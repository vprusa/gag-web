package cz.muni.fi.gag.web.dao;

import cz.muni.fi.gag.web.entity.DataLine;

import java.util.List;
import java.util.stream.Stream;

/**
 * 
 * @author Miloslav Zezulka, Vojtech Prusa
 *
 * {@link cz.muni.fi.gag.web.dao.impl.DataLineDaoImpl}
 * {@link cz.muni.fi.gag.web.dao.impl.DataLineGestureIterator}
 *
 *
 */
public interface DataLineDao extends GenericDao<DataLine> {

    List<DataLine> findByGestureId(long gestureId);

    Stream<DataLine> getStream(long gestureId);

    List<DataLine> getChunkForGesture(long gestureId, int offset, int limit);

    int removeBy(Long gestureId);

    List<DataLine> getInterestingTimes(Long gestureId);
}
