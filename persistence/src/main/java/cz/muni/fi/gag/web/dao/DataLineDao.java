package cz.muni.fi.gag.web.dao;

import java.util.Iterator;
import java.util.List;

import cz.muni.fi.gag.web.entity.DataLine;

/**
 * 
 * @author Miloslav Zezulka, Vojtech Prusa
 * 
 * @DataLineDaoImpl
 *
 */
public interface DataLineDao extends GenericDao<DataLine> {

    List<DataLine> findByGestureId(long gestureId);

    List<DataLine> getChunkForGesture(long gestureId, int offset, int max);

}
