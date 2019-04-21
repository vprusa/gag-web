package cz.muni.fi.gag.app.dao;

import java.util.List;

/**
 *
 * @author Miloslav Zezulka
 */
public interface GenericDao<T> {

    T create(final T entity);

    void remove(long id);

    T update(final T entity);

    T find(long id);

    List<T> findAll();
}
