/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.services.filters;

import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.services.service.DataLineService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 * <p>
 * This class takes filepath as input and filters just necessary data
 * out of it based on input parameters
 * <p>
 * Input & output file line format:
 * <p>
 * <YYYY-MM-DD_HH:mm:ss.SSS> <handId> <sensorId> <q0> <q1> <q2> <q3>
 * <a0> <a1> <a2>
 * <p>
 * g.e.:
 * <p>
 * 2019-01-08_11:55:39.386 * 1 0.70202637 -0.30603027 -0.31201172
 * 0.56225586 0.0 0.0 0.0
 * <p>
 * TODO fix samplesPerSensorPerSecond - its messed up and do not
 * contains values reliable considering variables name.. rename it?
 *
 */
//@SessionScoped
@ApplicationScoped
public class RecordedDataFilterImpl implements RecordedDataFilter {
    // TODO add class variables for filtering data ... do not pass them via method
    // parameters ...
    public final static Logger log = Logger.getLogger(RecordedDataFilterImpl.class.getSimpleName());

    @Inject
    private DataLineService dlService;

    List<DataLine> filteredData = null;
    BothHandsWrapper bhw = null;

    /**
     * Pls document this method.. also with ideas, TODO, etc. Parametrize whatever
     * you seem useful Using float type for samplesPerSensorPerSecond so i could
     * have gestures with rate in minutes as well
     */
    public void filter(Gesture orig, Gesture filtered, float samplesPerSensorPerSecond, boolean findEdges /* parameters */) {
        log.info("filtering: " + orig.toString()  + " -> " + filtered.toString() );

        filteredData = new ArrayList<DataLine>();
        bhw = new BothHandsWrapper((int) samplesPerSensorPerSecond * 2);

        Iterator<DataLine> dlli = orig.getData().iterator();

        DataLine dl = null;
        while (dlli.hasNext() && ((dl = dlli.next()) != null)) {
            if (isLineValid(dl, samplesPerSensorPerSecond, findEdges)) {
                filteredData.add(dl);
                dl.setGesture(filtered);
                dl.setId(null);
                dlService.create(dl);
                bhw.setLastFiltered(dl);
            }
            bhw.add(dl);
        }
    }

//    public void filter(Gesture ref, Gesture filtered, float samplesPerSensorPerSecond, boolean findEdges /* parameters */) {
//        filter(ref, filtered, samplesPerSensorPerSecond, findEdges);
//    }

    private boolean isLineValid(DataLine line, float samplesPerSensorPerSecond, boolean findEdges) {
        // check if time matches filtered samples rate per sensor on hand
        DataLine prevLine = bhw.getLastFiltered(line.getHandPosition(), line.getPosition());
        long timeToSkip = (long) (1000.0f * samplesPerSensorPerSecond);
        if (prevLine == null) {
            return true;
        }
        long lineDate = line.getTimestamp().getTime();
        long prevLineDate = prevLine.getTimestamp().getTime();
        long diffDates = lineDate - prevLineDate;
        if (diffDates > timeToSkip) {
            return true;
        }

        // TODO check if this value is edge value because then this and/or last one in
        // history of this sensor of hand has to added as well
        // using:
        // bhw.lastNDataOfEachSensorOfHand ..BothHandsWrapper.
        // bhw.get()
        // bhw.getTop(h, s)

        return false;
    }

    // TODO add methods that call filter(<parameters>) and name them smth like
    // filter<Basic|Minimal|Maximal|TimeOnly|etc.>(<parameters>) based on parameters
    // example
//    public void filterBasic(/* less parameters */) {
//        filter(orig, g, 0.5f, false);
//    }

    public void saveFilteredToFile(String filePath) throws IOException {
        saveFilteredToFile(filePath, false);
    }

    public void saveFilteredToFile(String filePath, boolean append) throws IOException {
        File file = new File(filePath);
        FileWriter fr = new FileWriter(file, append);
        BufferedWriter br = new BufferedWriter(fr);

        Iterator<DataLine> iter = filteredData.iterator();
        while (iter.hasNext()) {
            br.write(iter.next().toString() + (iter.hasNext() ? '\n' : ""));
        }
        br.close();
        fr.close();
    }
}
