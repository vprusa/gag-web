package cz.muni.fi.gag.web.services.service;

import cz.muni.fi.gag.web.persistence.entity.User;
import cz.muni.fi.gag.web.services.service.generic.GenericCRUDService;
import cz.muni.fi.gag.web.services.service.impl.UserServiceImpl;
import cz.muni.fi.gag.web.persistence.dao.UserDao;

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
