package cz.muni.fi.gag.web.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import cz.muni.fi.gag.web.dao.GenericDao;

/**
 * This class provides the most primitive and straight-forward implementation
 * of persistence.
 * @author Miloslav Zezulka
 *
 */
public abstract class AbstractGenericDao<T> implements GenericDao<T> {
    
		@Inject
	    protected EntityManager em;
	
	    private final Class<T> type;
	
	    public AbstractGenericDao(Class<T> entityType) {
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
	        em.remove(em.getReference(type, id));
	    }
	
	    @Override
	    public Optional<T> find(long id) {
	        T result = em.find(type, id);
	        return (Optional<T>) (result == null ? Optional.empty() : Optional.ofNullable(result));
	    }
	
	    @Override
	    public List<T> findAll() {
	        return em.createQuery("FROM " + type.getName(), type).getResultList();
	    }
}
