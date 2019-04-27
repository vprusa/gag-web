package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.dao.UserDao;
import cz.muni.fi.gag.web.entity.User;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class UserDaoImpl extends AbstractGenericDao<User> implements UserDao, Serializable {

    public UserDaoImpl() {
        super(User.class);
    }
}
