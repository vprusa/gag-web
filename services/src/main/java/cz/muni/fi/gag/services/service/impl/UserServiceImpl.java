package cz.muni.fi.gag.services.service.impl;

import cz.muni.fi.gag.services.service.UserService;
import cz.muni.fi.gag.services.service.generic.GenericCRUDServiceImpl;
import cz.muni.fi.gag.web.dao.UserDao;
import cz.muni.fi.gag.web.entity.User;

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
