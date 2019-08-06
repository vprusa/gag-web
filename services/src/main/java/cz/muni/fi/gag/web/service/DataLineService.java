package cz.muni.fi.gag.web.service;

import cz.muni.fi.gag.web.dao.DataLineDao;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.service.generic.GenericCRUDService;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Vojtech Prusa
 *
 * {@link cz.muni.fi.gag.web.service.impl.DataLineServiceImpl}
 * {@link cz.muni.fi.gag.web.service.impl.FingerDataLineServiceImpl}
 * {@link cz.muni.fi.gag.web.service.impl.WristDataLineServiceImpl}
 */
public interface DataLineService extends GenericCRUDService<DataLine, DataLineDao> {

    List<DataLine> findByGestureId(long gestureId);

    Iterator<DataLine> initIteratorByGesture(long gestureId);

}
