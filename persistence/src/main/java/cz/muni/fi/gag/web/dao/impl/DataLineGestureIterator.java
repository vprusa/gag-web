package cz.muni.fi.gag.web.dao.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jboss.logging.Logger;

import cz.muni.fi.gag.web.dao.DataLineDao;
import cz.muni.fi.gag.web.entity.DataLine;

/**
 * @author Vojtech Prusa
 * 
 *         This Iterator for DataLines of certain gesture provides feature of
 *         loading chunks of DataLines at once
 *
 */
public class DataLineGestureIterator implements Iterator<DataLine> {

    public static Logger log = Logger.getLogger(DataLineGestureIterator.class.getSimpleName());
    
    public static final int ITERATOR_MAX_COUNT = 50;
    private final DataLineDao dataLineDao;
    private final long gestureId;
    private int offset = 0;
    private List<DataLine> dataLines = Collections.emptyList();

    public DataLineGestureIterator(DataLineDao dataLineDao, long gestureId) {
        this.dataLineDao = dataLineDao;
        this.gestureId = gestureId;
    }

    public void loadChunkIfPossible() {
        if (dataLines == null) {
            dataLines = dataLineDao.getChunkForGesture(gestureId, offset, ITERATOR_MAX_COUNT);
        } else if (!dataLines.iterator().hasNext()) {
            log.info("Not having enough: " + offset + " + " + dataLines.size());
            offset += dataLines.size();
            dataLines = dataLineDao.getChunkForGesture(gestureId, offset, ITERATOR_MAX_COUNT);
        }
    }

    @Override
    public boolean hasNext() {
        loadChunkIfPossible();
        return dataLines.iterator().hasNext();
    }

    @Override
    public DataLine next() {
        loadChunkIfPossible();
        return dataLines.iterator().next();
    }

}
