package cz.muni.fi.gag.web.dao.impl;

import cz.muni.fi.gag.web.dao.GenericDao;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * @author <a href="mailto:martin.styk@gmail.com">Martin Styk</a>
 */
public abstract class GenericDaoImpl<T> implements GenericDao<T>, Serializable {

    @Inject
    protected EntityManager em;

    private Class<T> type;

    public GenericDaoImpl(Class<T> entityType) {
        type = entityType;
    }

    @Override
    public T create(final T entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public T update(final T entity) {
        return em.merge(entity);
    }

    @Override
    public void remove(long id) {
        em.remove(this.em.getReference(type, id));
    }

    @Override
    public Optional<T> find(Long id) {
        return Optional.ofNullable(em.find(type, id));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return em.createQuery("FROM " + type.getName()).<T>getResultList();
    }

}