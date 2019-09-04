package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import cz.muni.fi.gag.web.dao.GestureDao;
import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.entity.User;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class GestureDaoImpl extends AbstractGenericDao<Gesture> implements GestureDao, Serializable {

    public GestureDaoImpl() {
        super(Gesture.class);
    }

    @Transactional
    @Override
    public List<Gesture> findByUser(User u) {
        TypedQuery<Gesture> q = em.createQuery("SELECT g FROM Gesture g WHERE user_id = :userId", Gesture.class)
                .setParameter("userId", u.getId());
        List<Gesture> results = q.getResultList();
        return results;
    }

    @Override
    public Gesture findRefById(Long u) {
        return em.getReference(Gesture.class, u);
    }

}
