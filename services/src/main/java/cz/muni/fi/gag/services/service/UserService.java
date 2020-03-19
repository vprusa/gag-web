package cz.muni.fi.gag.services.service;

import cz.muni.fi.gag.services.service.impl.UserServiceImpl;
import cz.muni.fi.gag.web.dao.UserDao;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.services.service.generic.GenericCRUDService;

/**
 *
 * @author Vojtech Prusa
 *
 * {@link UserServiceImpl}
 *
 */
public interface UserService extends GenericCRUDService<User, UserDao> {

    User findByIdentificator(String email);
    
}
