package cz.muni.fi.gag.web.services.service.generic;

import cz.muni.fi.gag.web.services.service.DataLineService;
import cz.muni.fi.gag.web.services.service.FingerDataLineService;
import cz.muni.fi.gag.web.services.service.GestureService;
import cz.muni.fi.gag.web.services.service.impl.DataLineServiceImpl;
import cz.muni.fi.gag.web.services.service.impl.FingerDataLineServiceImpl;
import cz.muni.fi.gag.web.services.service.impl.GestureServiceImpl;
import cz.muni.fi.gag.web.persistence.dao.GenericDao;

import java.util.List;
import java.util.Optional;

/**
 * @author Vojtech Prusa
 *
 * @param <T>
 * @param <TDao>
 * 
 * {@link DataLineService}
 * {@link DataLineServiceImpl}
 * 
 * {@link FingerDataLineService}
 * {@link FingerDataLineServiceImpl}
 * 
 * {@link GestureService}
 * {@link GestureServiceImpl}
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
