package cz.muni.fi.gag.web.dao.impl;

import cz.muni.fi.gag.web.dao.DataLineDao;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Miloslav Zezulka, Vojtech Prusa
 *
 * {@link cz.muni.fi.gag.web.service.impl.DataLineServiceImpl}
 * {@link cz.muni.fi.gag.web.service.impl.DataLineServiceImpl}
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
        log.info("getChunkForGesture: gestureId: " + gestureId);
        return em
                .createQuery("SELECT g FROM DataLine g WHERE gesture_id = :gestureId", // ORDER BY timestamp ASC
                        DataLine.class)
                .setParameter("gestureId", gestureId).getResultStream();
    }

    //@Transactional
    @Transactional
    @Override
    public List<DataLine> getChunkForGesture(long gestureId, int offset, int max) {
        log.info("getChunkForGesture: gestureId: " + gestureId + " offset: " + offset + " max: " + max);
        List<DataLine> dll = em
                //.createQuery("SELECT g FROM DataLine g WHERE gesture_id = :gestureId ORDER BY timestamp ASC",
                .createQuery("SELECT g FROM DataLine g WHERE gesture_id = :gestureId",
                        DataLine.class)
                //.setParameter("gestureId", gestureId).setFirstResult(offset).setMaxResults(max).getResultList();
                .setParameter("gestureId", gestureId).getResultList();
        FingerDataLine[] dla = new FingerDataLine[dll.size()];
        dla = (FingerDataLine[]) dll.toArray();

        log.info("dll.toArray(): " +  dla.length);
        for(FingerDataLine dl : dla){
            log.info("DL: " + dl.toString());
        }
        return dll;
    }

    @Override
    public int removeBy(Long gestureId) {
        log.info("removeBy: gestureId: " + gestureId);
        return  em.createQuery("DELETE FROM DataLine g WHERE gesture_id = :gestureId")
            .setParameter("gestureId", gestureId).executeUpdate();
    }

    @Override
    public List<DataLine> getInterestingTimes(Long gestureId) {
        String ql = "SELECT min(dl.timestamp), max(dl.timestamp), max(dl.timestamp) - min(dl.timestamp) from DataLine" +
                " as dl where dl.gesture_id = :gestureId order by dl.timestamp;";
        // TODO add by gestureId AND userId ?
        // select * from DataLine as dl where dl.gesture_id = 27 and dl.timestamp in (select min(dl2.timestamp)
        // from DataLine as dl2 where dl2.gesture_id = 27);
        // select * from DataLine as dl where dl.gesture_id = 27 and dl.timestamp in (select max(dl2.timestamp)
        // from DataLine as dl2 where dl2.gesture_id = 27);
        TypedQuery<DataLine> q = em.createQuery(ql, DataLine.class)
                .setParameter("gestureId", gestureId);
        List<DataLine> results = q.getResultList();
        return results;
    }

}
