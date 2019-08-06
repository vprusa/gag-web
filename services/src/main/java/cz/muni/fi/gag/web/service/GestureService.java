package cz.muni.fi.gag.web.service;

import cz.muni.fi.gag.web.dao.GestureDao;
import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.web.service.generic.GenericCRUDService;

import java.util.List;

/**
 *
 * @author Vojtech Prusa
 *
 * {@link cz.muni.fi.gag.web.service.impl.GestureServiceImpl}
 *
 */
public interface GestureService extends GenericCRUDService<Gesture, GestureDao> {

    List<Gesture> findByUser(User u);
    
}
