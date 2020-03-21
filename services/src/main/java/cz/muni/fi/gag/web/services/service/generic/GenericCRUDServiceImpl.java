package cz.muni.fi.gag.web.services.service.generic;

import cz.muni.fi.gag.web.services.logging.Log;
import cz.muni.fi.gag.web.services.service.impl.DataLineServiceImpl;
import cz.muni.fi.gag.web.services.service.impl.FingerDataLineServiceImpl;
import cz.muni.fi.gag.web.persistence.dao.GenericDao;
import cz.muni.fi.gag.web.persistence.entity.GenericEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author Vojtech Prusa
 *
 * @param <T>
 * @param <TDao>
 * 
 * {@link DataLineServiceImpl}
 * {@link FingerDataLineServiceImpl}
 * 
 */
public abstract class GenericCRUDServiceImpl<T extends GenericEntity, TDao extends GenericDao<T>>
        implements GenericCRUDService<T, TDao> {

    @Override
    public T create(T entity) {
        T created = this.getDao().create(entity);
        Log.infoCreate(this.getClass(), created);
        return created;
    }

    @Override
    public T update(T entity) {
        T updated = getDao().update(entity);
        Log.infoUpdate(this.getClass(), updated);
        return getDao().update(entity);
    }

    @Override
    public void remove(T entity) throws IllegalArgumentException {
        getDao().remove(entity.getId());
        Log.infoRemove(this.getClass(), entity);
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
