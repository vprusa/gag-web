package cz.muni.fi.gag.web.service;

import cz.muni.fi.gag.web.dao.UserDao;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.web.service.generic.GenericCRUDService;

/**
 *
 * @author Vojtech Prusa
 *
 * @UserServiceImpl
 *
 */
public interface UserService extends GenericCRUDService<User, UserDao> {

}
