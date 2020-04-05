package cz.muni.fi.gag.web.services.recognition;

import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;

/**
 * @author Vojtech Prusa
 *
 * This forces any Comparator class to implement compare method
 */
public interface GestureComparator<T extends FingerDataLine> {
    GestureMatcher compare(T dl);
}
