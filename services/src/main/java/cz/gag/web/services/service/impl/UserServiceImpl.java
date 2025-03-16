package cz.gag.web.services.service.impl;

import cz.gag.web.persistence.entity.User;
import cz.gag.web.services.service.generic.GenericCRUDServiceImpl;
import cz.gag.web.services.service.UserService;
import cz.gag.web.persistence.dao.UserDao;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Vojtech Prusa
 *
 */
@Stateless
public class UserServiceImpl extends GenericCRUDServiceImpl<User, UserDao> implements UserService {

    @Inject
    protected UserDao genericDao;

    @Override
    public UserDao getDao() {
        return genericDao;
    }

    @Override
    public User findByIdentificator(String thirdPartyId) {
        // TODO validate email ?
        return getDao().findByThirdPartyId(thirdPartyId);
    }

}
