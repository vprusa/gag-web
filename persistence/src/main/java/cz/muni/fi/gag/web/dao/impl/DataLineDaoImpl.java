package cz.muni.fi.gag.web.dao.impl;

import cz.muni.fi.gag.web.dao.DataLineDao;
import cz.muni.fi.gag.web.entity.DataLine;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

/**
 * @author Miloslav Zezulka, Vojtech Prusa
 *
 * {@link cz.muni.fi.gag.web.service.impl.DataLineServiceImpl}
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

    /*
     * https://stackoverflow.com/questions/5067619/jpa-what-is-the-
     * proper-pattern-for-iterating-over-large-result-sets
     */
    @Transactional
    @Override
    public List<DataLine> getChunkForGesture(long gestureId, int offset, int max) {
        return em
                .createQuery("SELECT g FROM DataLine g WHERE gesture_id = :gestureId ORDER BY timestamp ASC",
                        DataLine.class)
                .setParameter("gestureId", gestureId).setFirstResult(offset).setMaxResults(max).getResultList();
    }

    /*
    private Iterator<DataLine> iterateAll(long gestureId) {
        int offset = 0;

        List<DataLine> models;
        while ((models = this.getAllModelsIterable(gestureId, offset, ITERATOR_MAX_COUNT)).size() > 0) {
            em.getTransaction().begin();
            models.iterator()
            for (DataLine model : models) {
                // log.info("do something with model: " + model.getId());
            }

            em.flush();
            em.clear();
            em.getTransaction().commit();
            offset += models.size();
        }
    }
    */

    // @Transactional
    // @Override
    // public Iterator<DataLine> iterateOverGestureData(long gestureId) {
    // TypedQuery<DataLine> q = em
    // .createQuery(, DataLine.class)
    // .setParameter("gestureId", gestureId);
    // List<DataLine> results = q.getgetgetResultList();
    /*
     * StatelessSession session = ((Session)
     * em.getDelegate()).getSessionFactory().openStatelessSession();
     * 
     * Query query =
     * session.createQuery("SELECT a FROM Address a WHERE .... ORDER BY a.id");
     * query.setFetchSize(Integer.valueOf(1000)); query.setReadOnly(true);
     * query.setLockMode("a", LockMode.NONE); ScrollableResults results =
     * query.scroll(ScrollMode.FORWARD_ONLY); while (results.next()) { Address addr
     * = (Address) results.get(0); // Do stuff } results.close(); session.close();
     */
    // return results;
    // }

}
