package cz.gag.web.persistence.dao;

import cz.gag.web.persistence.dao.impl.UserDaoImpl;
import cz.gag.web.persistence.entity.User;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

/**
 * 
 * @author Vojtech Prusa
 *
 * {@link UserDaoImpl}
 *
 */
public interface UserDao extends GenericDao<User> {

    User findByThirdPartyId(@NotNull String thirdPartyId);
    EntityManager getEm();

}
