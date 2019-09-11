package cz.muni.fi.gag.web.service;

import cz.muni.fi.gag.web.dao.DataLineDao;
import cz.muni.fi.gag.web.dao.impl.DataLineGestureIterator;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.service.generic.GenericCRUDService;
import java.util.List;
import java.util.stream.Stream;

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

    DataLineGestureIterator initIteratorByGesture(long gestureId);

    Stream<DataLine> getStream(long gestureId);

}
