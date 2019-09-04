package cz.muni.fi.gag.web.dao;

import java.util.List;

import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.entity.User;

/**
 * 
 * @author Vojtech Prusa
 * 
 * {@link cz.muni.fi.gag.web.dao.impl.GestureDaoImpl}
 *
 */
public interface GestureDao extends GenericDao<Gesture> {

    List<Gesture> findByUser(User u);

    Gesture findRefById(Long u);
}
