package cz.muni.fi.gag.web.dao;

import cz.muni.fi.gag.web.entity.DataLine;

import java.util.List;

/**
 * 
 * @author Miloslav Zezulka, Vojtech Prusa
 * 
 * {@link cz.muni.fi.gag.web.dao.impl.DataLineDaoImpl}
 *
 */
public interface DataLineDao extends GenericDao<DataLine> {

    List<DataLine> findByGestureId(long gestureId);

    List<DataLine> getChunkForGesture(long gestureId, int offset, int max);

}
