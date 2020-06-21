package cz.muni.fi.gag.web.services.recognition;

import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.services.recognition.matchers.SingleSensorGestureMatcher;

import java.io.Serializable;
import java.util.List;

/**
 * @author Vojtech Prusa
 *
 * This forces any Comparator class to implement compare method
 * TODO:
 * - move this to pacakge scala.shared.recognition.*
 * - change FingerDataLine with some interface based approach for reusage in (native) scala?
 */
public interface GestureMatchComparator<T extends FingerDataLine> extends Serializable {
    List<SingleSensorGestureMatcher> compare(T dl);
}
