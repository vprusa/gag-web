package cz.muni.fi.gag.web.persistence.dao.impl;

import cz.muni.fi.gag.web.persistence.dao.DataLineDao;
import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.persistence.entity.Sensor;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Miloslav Zezulka, Vojtech Prusa
 * <p>
 * {@link cz.muni.fi.gag.web.services.service.impl.DataLineServiceImpl}
 */
@ApplicationScoped
public class DataLineDaoImpl extends AbstractGenericDao<DataLine> implements DataLineDao, Serializable {

    public static Logger log = Logger.getLogger(DataLineGestureIterator.class.getSimpleName());

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
     *
     * TODO ... not working ...
     *
     * ERROR [org.hibernate.engine.jdbc.spi.SqlExceptionHelper] (Thread-824) IJ031040: Connection is not associated with a managed connection: org.jboss.jca.adapters.jdbc.jdk8.WrappedConnectionJDK8@3e7c33f6
     * ERROR [stderr] (Thread-824) Exception in thread "Thread-824" org.hibernate.exception.GenericJDBCException: could not advance using next()
     */
    @Transactional
    @Override
    public Stream<DataLine> getStream(long gestureId) {
//        log.trace("getChunkForGesture: gestureId: " + gestureId);
        return em
                .createQuery("SELECT g FROM DataLine g WHERE gesture_id = :gid", // ORDER BY timestamp ASC
                        DataLine.class)
                .setParameter("gid", gestureId).getResultStream();
    }


    @Transactional
    @Override
    public List<DataLine> getChunkForGesture(long gestureId, int offset, int limit) {
        log.trace("getChunkForGesture: gestureId: " + gestureId + " offset: " + offset + " limit: " + limit);
        List<DataLine> dll = em
                .createQuery("SELECT dl FROM DataLine dl WHERE gesture_id = :gid", DataLine.class)
                .setParameter("gid", gestureId).setFirstResult(offset).setMaxResults(limit).getResultList();
        return dll;
    }

    @Transactional
    @Override
    public List<DataLine> getChunkForGesture(long gestureId, int offset, int limit, Sensor s) {
        log.trace("getChunkForGesture: gestureId: " + gestureId + " offset: " + offset + " limit: " + limit + " s: " + s);
        List<DataLine> dll = em
                .createQuery("SELECT dl FROM DataLine dl WHERE gesture_id = :gid and position = :s", DataLine.class)
                .setParameter("gid", gestureId).setParameter("s", s)
                .setFirstResult(offset).setMaxResults(limit).getResultList();
        return dll;
    }

    @Override
    public int removeBy(Long gestureId) {
        log.trace("removeBy: gestureId: " + gestureId);
        return em.createQuery("DELETE FROM DataLine dl WHERE gesture_id = :gid")
                .setParameter("gid", gestureId).executeUpdate();
    }

    @Override
    public List<DataLine> getInteresting(Long gestureId) {
        // TODO add by gestureId AND userId ?
        final String qlMin = "SELECT dl FROM DataLine dl WHERE gesture_id = :gid AND timestamp IN " +
                "(SELECT MIN(timestamp) FROM DataLine where gesture_id = :gid) ";
        final String qlMax = "SELECT dl FROM DataLine dl WHERE gesture_id = :gid AND timestamp IN " +
                "(SELECT MAX(timestamp) FROM DataLine where gesture_id = :gid)";
        TypedQuery<DataLine> qMin = em.createQuery(qlMin, DataLine.class)
                .setParameter("gid", gestureId);
        List<DataLine> results = new ArrayList<DataLine>(qMin.getResultList());
        TypedQuery<DataLine> qMax = em.createQuery(qlMax, DataLine.class)
                .setParameter("gid", gestureId);
        results.addAll(qMax.getResultList());
        return results;
    }

    @Override
    public long getSize(long gestureId, Sensor s) {
        log.trace("getSize: gestureId: " + gestureId + " s: " + s);
//        Long count = em.createQuery("SELECT count(dl) FROM DataLine dl WHERE gesture_id = :gid and position = :s",
//                Long.class).setParameter("gid", gestureId).setParameter("s", s).getSingleResult();
        // https://stackoverflow.com/questions/1372317/how-do-we-count-rows-using-older-versions-of-hibernate-2009/42235791#42235791
        // TODO cast to Number may not be needed?
        long count =
                ((Number) em.createQuery("SELECT count(dl) FROM DataLine dl WHERE gesture_id = :gid and position = :s")
                .setParameter("gid", gestureId).setParameter("s", s).getSingleResult()).longValue();

        return count;
    }

    @Override
    public long getSize(long gestureId) {
        long count =
                ((Number) em.createQuery("SELECT count(dl) FROM DataLine dl WHERE gesture_id = :gid")
                        .setParameter("gid", gestureId).getSingleResult()).longValue();
        return count;
    }

}
