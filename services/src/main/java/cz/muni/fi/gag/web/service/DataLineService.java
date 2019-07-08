package cz.muni.fi.gag.web.service;

import java.util.List;

import cz.muni.fi.gag.web.dao.DataLineDao;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.service.generic.GenericCRUDService;

/**
 *
 * @author Vojtech Prusa
 *
 * @DataLineServiceImpl
 *
 */
public interface DataLineService extends GenericCRUDService<DataLine, DataLineDao> {

    List<DataLine> findByGestureId(long gestureId);

}
