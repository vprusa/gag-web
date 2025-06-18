package cz.gag.web.persistence.dao;

import cz.gag.web.persistence.dao.impl.GestureDaoImpl;
import cz.gag.web.persistence.entity.Gesture;
import cz.gag.web.persistence.entity.User;

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

    List<Gesture> findActive(User u);

    Gesture deactivateAllExcept(Gesture g);
}
