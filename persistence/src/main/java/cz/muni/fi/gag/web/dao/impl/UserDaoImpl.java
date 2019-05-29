package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.NonUniqueResultException;
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
    public User findByThirdPartyId(@NotNull String thirdPartyId) {
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.thirdPartyId = :thirdPartyId", User.class)
                .setParameter("thirdPartyId", thirdPartyId);
        List<User> results = q.getResultList();
        if (results.isEmpty())
            return null;
        else if (results.size() == 1)
            return results.get(0);
        throw new NonUniqueResultException();
        // return q.getSingleResult();
    }
}
