package cz.muni.fi.gag.web.service.generic;

import cz.muni.fi.gag.web.dao.GenericDao;
import cz.muni.fi.gag.web.entity.AbstractEntity;
import cz.muni.fi.gag.web.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * @author Vojtech Prusa
 *
 * @param <T>
 * @param <TDao>
 * 
 * {@link cz.muni.fi.gag.web.service.impl.DataLineServiceImpl}
 * {@link cz.muni.fi.gag.web.service.impl.FingerDataLineServiceImpl}
 * 
 */
public abstract class GenericCRUDServiceImpl<T extends AbstractEntity, TDao extends GenericDao<T>>
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
