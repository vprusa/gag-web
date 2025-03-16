package cz.gag.web.persistence.dao.impl;

import cz.gag.web.persistence.dao.GestureDao;
import cz.gag.web.persistence.entity.Gesture;
import cz.gag.web.persistence.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class GestureDaoImpl extends AbstractGenericDao<Gesture> implements GestureDao, Serializable {

    public GestureDaoImpl() {
        super(Gesture.class);
    }

//    @Transactional
    @Override
    public List<Gesture> findByUser(User u) {
//        TypedQuery<Gesture> q = em.createQuery("SELECT g FROM Gesture g JOIN FETCH g.data WHERE user_id = :userId", Gesture.class)
        TypedQuery<Gesture> q = em.createQuery("SELECT g FROM Gesture g WHERE user_id = :userId", Gesture.class)
                .setParameter("userId", u.getId());
        List<Gesture> results = q.getResultList();
        return results;
    }

    @Override
    public Gesture findRefById(Long u) {
//        TypedQuery<Gesture> q = em.createQuery("SELECT g FROM Gesture g LEFT JOIN FETCH g.data WHERE g.id = :id", Gesture.class)
        TypedQuery<Gesture> q = em.createQuery("SELECT g FROM Gesture g WHERE g.id = :id", Gesture.class)
                .setParameter("id", u);
        Gesture result = q.getSingleResult();
        return result;
//        return em.getReference(Gesture.class, u);
    }

    @Override
    public List<Gesture> findActive(User u) {
//        return getDao().findActive();
        TypedQuery<Gesture> q = em.createQuery("SELECT g FROM Gesture g WHERE user_id = :userId AND isActive = 1", Gesture.class)
                .setParameter("userId", u.getId());
        List<Gesture> results = q.getResultList();
        return results;
    }

}
