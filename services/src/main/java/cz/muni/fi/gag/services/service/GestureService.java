package cz.muni.fi.gag.services.service;

import cz.muni.fi.gag.services.service.impl.GestureServiceImpl;
import cz.muni.fi.gag.web.dao.GestureDao;
import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.services.service.generic.GenericCRUDService;

import java.util.List;

/**
 *
 * @author Vojtech Prusa
 *
 * {@link GestureServiceImpl}
 *
 */
public interface GestureService extends GenericCRUDService<Gesture, GestureDao> {

    List<Gesture> findByUser(User u);
    Gesture findRefById(Long u);

}
