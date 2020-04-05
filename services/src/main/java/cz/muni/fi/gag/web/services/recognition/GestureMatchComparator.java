package cz.muni.fi.gag.web.services.recognition;

import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;

/**
 * @author Vojtech Prusa
 *
 * This forces any Comparator class to implement compare method
 * TODO:
 * - move this to pacakge scala.shared.recognition.*
 * - change FingerDataLine with some interface based approach for reusage in (native) scala?
 */
public interface GestureMatchComparator<T extends FingerDataLine> {
    GestureMatcher compare(T dl);
}
