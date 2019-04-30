package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

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

    @Override
    public User findByEmail(@NotNull String email) {
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email);
        return q.getSingleResult();
    }
}
