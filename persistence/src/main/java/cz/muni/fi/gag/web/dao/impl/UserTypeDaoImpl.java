package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.dao.UserTypeDao;
import cz.muni.fi.gag.web.entity.UserType;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class UserTypeDaoImpl extends AbstractGenericDao<UserType> implements UserTypeDao, Serializable {

    public UserTypeDaoImpl() {
        super(UserType.class);
    }
}
