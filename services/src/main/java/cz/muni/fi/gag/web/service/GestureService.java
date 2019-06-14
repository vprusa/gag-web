package cz.muni.fi.gag.web.service;

import java.util.List;

import cz.muni.fi.gag.web.dao.GestureDao;
import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.web.service.generic.GenericCRUDService;

/**
 *
 * @author Vojtech Prusa
 *
 * @GestureServiceImpl
 *
 */
public interface GestureService extends GenericCRUDService<Gesture, GestureDao> {

    List<Gesture> findByUser(User u);
    
}
