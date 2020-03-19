/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.services.filters;

import cz.muni.fi.gag.services.service.DataLineService;
import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.entity.Hand;
import cz.muni.fi.gag.web.entity.Sensor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 *         This class takes filepath as input and filters just necessary data
 *         out of it based on input parameters
 * 
 *         Input & output file line format:
 * 
 *         <YYYY-MM-DD_HH:mm:ss.SSS> <handId> <sensorId> <q0> <q1> <q2> <q3>
 *         <a0> <a1> <a2>
 * 
 *         g.e.:
 * 
 *         2019-01-08_11:55:39.386 * 1 0.70202637 -0.30603027 -0.31201172
 *         0.56225586 0.0 0.0 0.0
 * 
 *         TODO fix samplesPerSensorPerSecond - its messed up and do not
 *         contains values reliable considering variables name.. rename it?
 *
 */
@ApplicationScoped
public class RecordedDataFilter<T extends DataLine> {
    // TODO add class variables for filtering data ... do not pass them via method
    // parameters ...
    public final static Logger log = Logger.getLogger(RecordedDataFilter.class.getSimpleName());



    @Inject
    private DataLineService dlService;

    private Gesture orig;
    private Gesture g;

    public RecordedDataFilter(Gesture orig, Gesture g){
        this.orig = orig;
        this.g = g;
    }

    class HistoryArrayList<T> extends ArrayList<T> {
        int historyCount = 25;

        public HistoryArrayList(int historyCount) {
            this.historyCount = historyCount;
        }

        @Override
        public boolean add(T e) {
            if (this.size() > historyCount) {
                this.remove(this.size() - 1);
            }
            return super.add(e);
        }
    }

    class BothHandsWrapper {

        int historyCountPerSensorOnHand = 25;

        public Map<Hand, Map<Sensor, List<DataLine>>> lastNDataOfEachSensorOfHand = new HashMap<Hand, Map<Sensor, List<DataLine>>>();

        public Map<Hand, Map<Sensor, DataLine>> lastFilteredDataOfEachSensorOfHand = new HashMap<Hand, Map<Sensor, DataLine>>();

        public BothHandsWrapper(int historyCountPerSensorOnHand) {
            this.historyCountPerSensorOnHand = historyCountPerSensorOnHand;
            for (Hand h : Hand.values()) {
                HashMap<Sensor, List<DataLine>> lastNDataOfEachSensor = new HashMap<Sensor, List<DataLine>>();
                lastNDataOfEachSensorOfHand.put(h, lastNDataOfEachSensor);

                HashMap<Sensor, DataLine> lastFilteredDataOfEachSensor = new HashMap<Sensor, DataLine>();
                lastFilteredDataOfEachSensorOfHand.put(h, lastFilteredDataOfEachSensor);

                for (Sensor s : Sensor.values()) {
                    lastNDataOfEachSensor.put(s, new HistoryArrayList<DataLine>(historyCountPerSensorOnHand));
                    lastFilteredDataOfEachSensor.put(s, null);
                }
            }
        }

        public void add(DataLine line) {
            lastNDataOfEachSensorOfHand.get(line.getHandPosition()).get(line.getPosition()).add(line);
        }

        public DataLine getTop(Hand h, Sensor s) {
            return get(h, s, 0);
        }

        public DataLine get(Hand h, Sensor s, int i) {
            int size = lastNDataOfEachSensorOfHand.get(h).get(s).size();
            if (i >= 0 && size > i)
                return lastNDataOfEachSensorOfHand.get(h).get(s).get(size - 1 - i);
            return null;
        }

        public DataLine getLastFiltered(Hand h, Sensor s) {
            return bhw.lastFilteredDataOfEachSensorOfHand.get(h).get(s);
        }

        public void setLastFiltered(DataLine line) {
            lastFilteredDataOfEachSensorOfHand.get(line.getHandPosition()).put(line.getPosition(), line);
        }
    }

    List<DataLine> filteredData = null;
    BothHandsWrapper bhw = null;

    /**
     * Pls document this method.. also with ideas, TODO, etc. Parametrize whatever
     * you seem useful Using float type for samplesPerSensorPerSecond so i could
     * have gestures with rate in minutes as well
     */
    public void filter(float samplesPerSensorPerSecond, boolean findEdges /* parameters */) {
        filteredData = new ArrayList<DataLine>();
        bhw = new BothHandsWrapper((int) samplesPerSensorPerSecond * 2);

        Iterator<DataLine> dlli = orig.getData().iterator();
        DataLine dl = null;
        while ((dl = dlli.next()) != null) {
            if (isLineValid(dl, samplesPerSensorPerSecond, findEdges)) {
                filteredData.add(dl);
                dl.setGesture(g);
                //g.addDataLine(dl);
                dlService.create(dl);
                bhw.setLastFiltered(dl);
            }
            bhw.add(dl);
        }
    }

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
    public void filterBasic(/* less parameters */) {
        filter(0.5f, false);
    }

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
