package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.dao.GestureDao;
import cz.muni.fi.gag.web.entity.Gesture;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class GestureDaoImpl extends AbstractGenericDao<Gesture> implements GestureDao, Serializable {

    public GestureDaoImpl() {
        super(Gesture.class);
    }
}
