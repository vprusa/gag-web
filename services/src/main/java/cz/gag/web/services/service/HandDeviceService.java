package cz.gag.web.services.service;

import cz.gag.web.services.service.generic.GenericCRUDService;
import cz.gag.web.persistence.dao.HandDeviceDao;
import cz.gag.web.persistence.entity.HandDevice;

/**
 *
 * @author Vojtech Prusa
 *
 * @HandDeviceServiceImpl
 *
 */
public interface HandDeviceService extends GenericCRUDService<HandDevice, HandDeviceDao> {

}
