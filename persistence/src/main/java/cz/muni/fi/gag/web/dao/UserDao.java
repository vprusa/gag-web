package cz.muni.fi.gag.web.dao;

import cz.muni.fi.gag.web.entity.User;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

/**
 * 
 * @author Vojtech Prusa
 *
 * {@link cz.muni.fi.gag.web.dao.impl.UserDaoImpl}
 *
 */
public interface UserDao extends GenericDao<User> {

    User findByThirdPartyId(@NotNull String thirdPartyId);
    EntityManager getEm();

}
