package cz.muni.fi.gag.services.service;

import cz.muni.fi.gag.services.service.impl.FingerDataLineServiceImpl;
import cz.muni.fi.gag.web.dao.FingerDataLineDao;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.services.service.generic.GenericCRUDService;

/**
 *
 * @author Vojtech Prusa
 *
 * {@link FingerDataLineServiceImpl}
 *
 */
public interface FingerDataLineService extends GenericCRUDService<FingerDataLine, FingerDataLineDao> {

}
