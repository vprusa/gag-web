package cz.muni.fi.gag.web.services.recognition;

import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;

import java.io.Serializable;

/**
 * @author Vojtech Prusa
 *
 * This forces any Comparator class to implement compare method
 * TODO:
 * - move this to pacakge scala.shared.recognition.*
 * - change FingerDataLine with some interface based approach for reusage in (native) scala?
 */
public interface GestureMatchComparator<T extends FingerDataLine> extends Serializable {
    GestureMatcher compare(T dl);
}
