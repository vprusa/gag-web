package cz.gag.web.services.service;

import cz.gag.web.persistence.entity.User;
import cz.gag.web.services.service.generic.GenericCRUDService;
import cz.gag.web.services.service.impl.UserServiceImpl;
import cz.gag.web.persistence.dao.UserDao;

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
