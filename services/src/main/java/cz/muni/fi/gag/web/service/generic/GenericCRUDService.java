package cz.muni.fi.gag.web.service.generic;

import cz.muni.fi.gag.web.dao.GenericDao;
import java.util.List;
import java.util.Optional;

/**
 * @author Vojtech Prusa
 *
 * @param <T>
 * @param <TDao>
 * 
 * {@link cz.muni.fi.gag.web.service.DataLineService}
 * {@link cz.muni.fi.gag.web.service.impl.DataLineServiceImpl}
 * 
 * {@link cz.muni.fi.gag.web.service.FingerDataLineService}
 * {@link cz.muni.fi.gag.web.service.impl.FingerDataLineServiceImpl}
 * 
 * {@link cz.muni.fi.gag.web.service.GestureService}
 * {@link cz.muni.fi.gag.web.service.impl.GestureServiceImpl}
 * 
 */
public interface GenericCRUDService<T, TDao extends GenericDao<T>> {

    T create(T entity);

    T update(T entity);

    void remove(T entity);

    Optional<T> findById(Long id);

    List<T> findAll();
    
    TDao getDao();
   /* default TDao getDao() {
        return null;
    };*/
    
}
