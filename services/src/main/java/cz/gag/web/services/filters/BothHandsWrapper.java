package cz.gag.web.services.filters;

import cz.gag.web.persistence.entity.DataLine;
import cz.gag.web.persistence.entity.Hand;
import cz.gag.web.persistence.entity.Sensor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/* TODO
    Caused by: org.infinispan.commons.marshall.NotSerializableException: cz.gag.web.services.filters.RecordedDataFilterImpl$BothHandsWrapper
Caused by: an exception which occurred:
	in field cz.gag.web.services.filters.RecordedDataFilterImpl.bhw
	in object cz.gag.web.services.filters.RecordedDataFilterImpl@22962d8c
	in field org.jboss.weld.contexts.SerializableContextualInstanceImpl.instance
	in object org.jboss.weld.contexts.SerializableContextualInstanceImpl@73c304fe
	in object org.jboss.weld.contexts.SerializableContextualInstanceImpl@73c304fe
	in object java.util.HashMap@9dd8a28

	quickfix implements Serializable
	Q: enough?
	After app started failing to start ..  WELD-000227 and WELD-1887

    Fixed starting wildfly with:
	 -Dorg.jboss.weld.serialization.beanIdentifierIndexOptimization=false
 */

public class BothHandsWrapper implements Serializable {

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
        return this.lastFilteredDataOfEachSensorOfHand.get(h).get(s);
    }

    public void setLastFiltered(DataLine line) {
        lastFilteredDataOfEachSensorOfHand.get(line.getHandPosition()).put(line.getPosition(), line);
    }
}