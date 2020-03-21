package cz.muni.fi.gag.web.services.service;

import cz.muni.fi.gag.web.services.service.generic.GenericCRUDService;
import cz.muni.fi.gag.web.services.service.impl.FingerDataLineServiceImpl;
import cz.muni.fi.gag.web.persistence.dao.FingerDataLineDao;
import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;

/**
 *
 * @author Vojtech Prusa
 *
 * {@link FingerDataLineServiceImpl}
 *
 */
public interface FingerDataLineService extends GenericCRUDService<FingerDataLine, FingerDataLineDao> {

}
