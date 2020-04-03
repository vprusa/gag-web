package cz.muni.fi.gag.web.persistence.dao.impl;

import cz.muni.fi.gag.web.persistence.dao.DataLineDao;
import cz.muni.fi.gag.web.persistence.entity.DataLine;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.jboss.logging.Logger;

/**
 * @author Vojtech Prusa
 * 
 *         This Iterator for DataLines of certain gesture provides feature of
 *         loading chunks of DataLines at once
 *
 */
public class DataLineGestureIterator implements Iterator<DataLine> {

    public static Logger log = Logger.getLogger(DataLineGestureIterator.class.getSimpleName());
    
    public static final int ITERATOR_MAX_COUNT = 5;
    private final DataLineDao dataLineDao;
    private final long gestureId;
    private int offset = 0;
    private Stream<DataLine> dataLinesStream = Stream.empty();
    private List<DataLine> dll = Collections.emptyList();
    private Iterator<DataLine> dlli = Collections.emptyIterator();

    public DataLineGestureIterator(DataLineDao dataLineDao, long gestureId) {
        this.dataLineDao = dataLineDao;
        this.gestureId = gestureId;
    }

    public void loadChunkIfPossible() {
        log.trace("loadChunkIfPossible: ");
        if (dll == null || dll.isEmpty()) {
            dll = dataLineDao.getChunkForGesture(gestureId, offset, ITERATOR_MAX_COUNT);
            log.trace("First load: " + offset + " + " + dll.size());
        } else if (!dll.iterator().hasNext()) {
            log.trace("Not having enough: " + offset + " + " + dll.size());
            offset += dll.size();
            dll = dataLineDao.getChunkForGesture(gestureId, offset, ITERATOR_MAX_COUNT);
        }
        //dataLinesList = dataLineDao.getChunkForGesture(gestureId);
    }

    @Override
    public boolean hasNext() {
        log.trace("hasNext");
        loadChunkIfPossible();
        return dll.iterator().hasNext();
    }

    @Override
    public DataLine next() {
        log.trace("next");
        if(hasNext()) {
            DataLine dl = dll.iterator().next();
            log.trace("next - dl: " + dl.toString());
            return dl;
        }
        return null;
    }

}
