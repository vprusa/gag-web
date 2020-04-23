package cz.muni.fi.gag.web.persistence.dao;

import cz.muni.fi.gag.web.persistence.dao.impl.GestureDaoImpl;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.User;

import java.util.List;

/**
 * 
 * @author Vojtech Prusa
 * 
 * {@link GestureDaoImpl}
 *
 */
public interface GestureDao extends GenericDao<Gesture> {

    List<Gesture> findByUser(User u);

    Gesture findRefById(Long u);

    List<Gesture> findUsersActive(User u);
}
