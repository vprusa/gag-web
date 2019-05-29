package cz.muni.fi.gag.web.dao;

import javax.validation.constraints.NotNull;

import cz.muni.fi.gag.web.entity.User;

/**
 * 
 * @author Vojtech Prusa
 * 
 * @UserDaoImpl
 *
 */
public interface UserDao extends GenericDao<User> {

    User findByThirdPartyId(@NotNull String thirdPartyId);

}
