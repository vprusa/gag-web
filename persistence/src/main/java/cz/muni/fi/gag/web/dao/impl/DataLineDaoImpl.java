package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import cz.muni.fi.gag.web.dao.DataLineDao;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.entity.User;

/**
 * @author Miloslav Zezulka, Vojtech Prusa
 *
 * @DataLineServiceImpl
 */
@ApplicationScoped
public class DataLineDaoImpl extends AbstractGenericDao<DataLine> implements DataLineDao, Serializable {

    public DataLineDaoImpl() {
        super(DataLine.class);
    }

    @Transactional
    @Override
    public List<DataLine> findByGestureId(long gestureId) {
        TypedQuery<DataLine> q = em
                .createQuery("SELECT g FROM DataLine g WHERE gesture_id = :gestureId", DataLine.class)
                .setParameter("gestureId", gestureId);
        List<DataLine> results = q.getResultList();
        return results;
    }

}
