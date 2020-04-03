package cz.muni.fi.gag.web.persistence.dao.impl;

import cz.muni.fi.gag.web.persistence.dao.DataLineDao;
import cz.muni.fi.gag.web.persistence.entity.DataLine;
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
 *
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
                .createQuery("SELECT g FROM DataLine g WHERE gesture_id = :gestureId", // ORDER BY timestamp ASC
                        DataLine.class)
                .setParameter("gestureId", gestureId).getResultStream();
    }

    //@Transactional
    @Transactional
    @Override
    public List<DataLine> getChunkForGesture(long gestureId, int offset, int max) {
//        log.trace("getChunkForGesture: gestureId: " + gestureId + " offset: " + offset + " max: " + max);
        List<DataLine> dll = em
                .createQuery("SELECT g FROM DataLine g WHERE gesture_id = :gestureId",
                        DataLine.class)
                .setParameter("gestureId", gestureId).getResultList();
//        FingerDataLine[] dla = new FingerDataLine[dll.size()];
//        dla = (FingerDataLine[]) dll.toArray();
//        log.trace("dll.toArray(): " +  dla.length);
//        for(FingerDataLine dl : dla){
//            log.trace("DL: " + dl.toString());
//        }
        return dll;
    }

    @Override
    public int removeBy(Long gestureId) {
        log.trace("removeBy: gestureId: " + gestureId);
        return  em.createQuery("DELETE FROM DataLine g WHERE gesture_id = :gestureId")
            .setParameter("gestureId", gestureId).executeUpdate();
    }

    @Override
    public List<DataLine> getInteresting(Long gestureId) {
        // TODO add by gestureId AND userId ?
        final String qlMin = "SELECT dl FROM DataLine dl WHERE gesture_id = :gestureId AND timestamp IN " +
                "(SELECT MIN(timestamp) FROM DataLine where gesture_id = :gestureId) ";
        final String qlMax = "SELECT dl FROM DataLine dl WHERE gesture_id = :gestureId AND timestamp IN " +
                "(SELECT MAX(timestamp) FROM DataLine where gesture_id = :gestureId)";
        TypedQuery<DataLine> qMin = em.createQuery(qlMin, DataLine.class)
                .setParameter("gestureId", gestureId);
        List<DataLine> results= new ArrayList<DataLine>(qMin.getResultList());
        TypedQuery<DataLine> qMax = em.createQuery(qlMax, DataLine.class)
                .setParameter("gestureId", gestureId);
        results.addAll(qMax.getResultList());
        return results;
    }

}
