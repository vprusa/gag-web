/*
Copyright (c) 2020 Vojtěch Průša
*/
package cz.gag.web.services.filters;

import cz.gag.web.persistence.entity.Gesture;

import java.io.Serializable;

/**
 * @author Vojtech Prusa
 *
 * {@link RecordedDataFilterImpl}
 */
public interface RecordedDataFilter extends Serializable /*WELD-000072*/ {
    void filter(Gesture orig, Gesture filtered, float samplesPerSensorPerSecond, boolean findEdges /* parameters */);
}
