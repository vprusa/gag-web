package cz.muni.fi.gag.web.dao;

import java.util.List;
import java.util.Optional;

/**
 * Provides basic operations with entities on the database level.
 *
 * @author Miloslav Zezulka
 */
public interface GenericDao<T> {

    /**
     * Creates a new entity of type {@code T}. The argument passed must not be null and
     * its id must be set to null.
     *
     * @throws IllegalArgumentException entity is null or the id is not null
     * @throws ConstraintViolationException any column constraints are violated
     * @param entity new entity to be inserted
     */
    T create(final T entity);

    /**
     * Removes an existing entity with the {@code id} passed.
     *
     * @throws IllegalArgumentException id does not describe an existing entity in the database
     * @param id database identifier of the entity
     */
    void remove(long id);

    /**
     * Updates an existing {@link entity} and returns the instance relevant to
     * the current persistence context.
     *
     * @param entity entity to be updated in the database, cannot be null
     * @throws IllegalArgumentException entity is null or its id is null
     * @throws ConstraintViolationException any column constraints are violated
     * @return the managed instance returned from the database
     */
    T update(final T entity);
    
    /**
     * Returns an entity of type {@link T} with the id {@code id}.
     *
     * @throws IllegalArgumentException id is null
     * @return Optional constructed with the entity of id {@code id} or
     * Optional.empty() if no such entry exists.
     */
    Optional<T> find(long id);
    
    /**
     * Returns all entries of the given type {@code T} present in the database.
     *
     * @return A {@link List} of all entries, an empty {@link List} if there
     * are none.
     */
    List<T> findAll();
}
