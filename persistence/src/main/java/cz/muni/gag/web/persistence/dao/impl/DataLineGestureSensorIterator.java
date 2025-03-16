package cz.gag.web.persistence.dao.impl;

import cz.gag.web.persistence.dao.DataLineDao;
import cz.gag.web.persistence.entity.DataLine;
import cz.gag.web.persistence.entity.Sensor;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * @author Vojtech Prusa
 * <p>
 * This Iterator for DataLines of certain gesture provides feature of
 * loading chunks of DataLines at once.
 * It may be used in future for:
 * - workaround of WildFly14 bug or missing feature for mysql iterator,
 * - (realtime) performance optimalization changing offset & max parameters (e.g. for dekstop app),
 * - native loading data from SD card if this is ever used as C(++) library for ESP32 based app, ...
 * </p>
 */
public class DataLineGestureSensorIterator extends DataLineGestureIterator {

    public static Logger log = Logger.getLogger(DataLineGestureSensorIterator.class.getSimpleName());

    protected final Sensor s;

    public DataLineGestureSensorIterator(DataLineDao dataLineDao, long gestureId, Sensor s) {
        super(dataLineDao, gestureId);
        this.s = s;
    }

    @Override
    public List<DataLine> getChunkForGesture(int offset) {
        return dataLineDao.getChunkForGesture(gestureId, offset, ITERATOR_MAX_COUNT, s);
    }

    @Override
    public long getTotalSize() {
        return dataLineDao.getSize(gestureId,s);
    }

}
