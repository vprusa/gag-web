package cz.muni.fi.gag.web.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import cz.muni.fi.gag.web.dao.GenericDao;

/**
 * This class provides the most primitive and straight-forward implementation of
 * persistence.
 * 
 * @author Miloslav Zezulka, Vojtech Prusa
 * 
 * @DataLineDaoImpl
 * @FingerSensorOffsetDaoImpl 
 * 
 * TODO add @Transactional as annotation on class itself not methods?
 */
public abstract class AbstractGenericDao<T> implements GenericDao<T> {

    //@Inject
    // TODO move to its own class?
    @PersistenceContext//(name="gagEntityManager")
    protected EntityManager em;
/*
    @Produces
    public EntityManager entityManager(){
      return em;
    }*/
    
    private final Class<T> type;

    public AbstractGenericDao(Class<T> entityType) {
        type = entityType;
    }

    @Transactional
    @Override
    public T create(final T entity) {
        em.persist(entity);
        return entity;
    }
    
    @Transactional
    @Override
    public T update(final T entity) {
        return em.merge(entity);
    }

    @Transactional
    @Override
    public void remove(long id) {
        em.remove(em.getReference(type, id));
    }

    @Transactional
    @Override
    public Optional<T> find(Long id) {
        T result = em.find(type, id);
        return (Optional<T>) (result == null ? Optional.empty() : Optional.ofNullable(result));
    }

    @Transactional
    @Override
    public List<T> findAll() {
        return em.createQuery("FROM " + type.getName(), type).getResultList();
    }
}
