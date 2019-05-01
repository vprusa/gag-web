package cz.muni.fi.gag.web.service.generic;

import java.util.List;
import java.util.Optional;

import cz.muni.fi.gag.web.dao.GenericDao;

import cz.muni.fi.gag.web.entity.AbstractEntity;
import cz.muni.fi.gag.web.logging.LogMessages;

/**
 * @author Vojtech Prusa
 *
 * @param <T>
 * @param <TDao>
 * 
 * @DataLineServiceImpl
 * @FingerDataLineServiceImpl
 * 
 */
public abstract class GenericCRUDServiceImpl<T extends AbstractEntity, TDao extends GenericDao<T>>
        implements GenericCRUDService<T, TDao> {

    @Override
    public T create(T entity) {
        T created = this.getDao().create(entity);
        LogMessages.infoCreate(this.getClass(), created);
        return created;
    }

    @Override
    public T update(T entity) {
        T updated = getDao().update(entity);
        LogMessages.infoUpdate(this.getClass(), updated);
        return getDao().update(entity);
    }

    @Override
    public void remove(T entity) throws IllegalArgumentException {
        getDao().remove(entity.getId());
        LogMessages.infoRemove(this.getClass(), entity);
    }

    @Override
    public Optional<T> findById(Long id) {
        return getDao().find(id);
    }

    @Override
    public List<T> findAll() {
        return getDao().findAll();
    }

}
