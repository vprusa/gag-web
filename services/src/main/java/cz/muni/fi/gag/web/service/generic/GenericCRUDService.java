package cz.muni.fi.gag.web.service.generic;

import java.util.List;
import java.util.Optional;

import cz.muni.fi.gag.web.dao.GenericDao;

/**
 * @author Vojtech Prusa
 *
 * @param <T>
 * @param <TDao>
 * 
 * @DataLineService
 * @DataLineServiceImpl
 * 
 * @FingerDataLineService
 * @FingerDataLineServiceImpl
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
