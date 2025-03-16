package cz.gag.web.persistence.dao.impl;

import cz.gag.web.persistence.dao.DataLineDao;
import cz.gag.web.persistence.entity.DataLine;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Vojtech Prusa
 * <p>
 * This Iterator for DataLines of certain gesture provides feature of
 * loading chunks of DataLines at once
 *
 * {@link DataLineGestureSensorIterator}
 */
public class DataLineGestureIterator implements Iterator<DataLine> {

    public static Logger log = Logger.getLogger(DataLineGestureIterator.class.getSimpleName());

    public static final int ITERATOR_MAX_COUNT = 6;
    protected final DataLineDao dataLineDao;
    protected final long gestureId;
    protected int offset = 0;
    protected Stream<DataLine> dataLinesStream = Stream.empty();
    protected List<DataLine> dll = Collections.emptyList();
    protected Iterator<DataLine> dlli = Collections.emptyIterator();

    public DataLineGestureIterator(DataLineDao dataLineDao, long gestureId) {
        this.dataLineDao = dataLineDao;
        this.gestureId = gestureId;
    }

    public void loadChunkIfPossible() {
        log.trace("loadChunkIfPossible: ");
        if (dll == null || dll.isEmpty()) {
            dll = getChunkForGesture(offset);
            dlli = dll.iterator();
            log.trace("First load: " + offset + " + " + dll.size());
        } else if (!dlli.hasNext()) {
            log.trace("Not having enough: " + offset + " + " + dll.size());
            offset += dll.size();
            dll = getChunkForGesture(offset);
            dlli = dll.iterator();
        }
        //dataLinesList = dataLineDao.getChunkForGesture(gestureId);
    }

    public List<DataLine> getChunkForGesture(int offset) {
        return dataLineDao.getChunkForGesture(gestureId, offset, ITERATOR_MAX_COUNT);
    }

    public long getTotalSize() {
        return dataLineDao.getSize(gestureId);
    }

    @Override
    public boolean hasNext() {
        log.trace("hasNext");
        loadChunkIfPossible();
        return dlli.hasNext();
    }

    @Override
    public DataLine next() {
        log.trace("next");
        DataLine dl = dlli.next();
        log.trace("next - dl: " + dl.toString());
        return dl;
    }

}
