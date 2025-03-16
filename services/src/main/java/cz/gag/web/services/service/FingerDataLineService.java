package cz.gag.web.services.service;

import cz.gag.web.services.service.generic.GenericCRUDService;
import cz.gag.web.services.service.impl.FingerDataLineServiceImpl;
import cz.gag.web.persistence.dao.FingerDataLineDao;
import cz.gag.web.persistence.entity.FingerDataLine;

/**
 *
 * @author Vojtech Prusa
 *
 * {@link FingerDataLineServiceImpl}
 *
 */
public interface FingerDataLineService extends GenericCRUDService<FingerDataLine, FingerDataLineDao> {
}
