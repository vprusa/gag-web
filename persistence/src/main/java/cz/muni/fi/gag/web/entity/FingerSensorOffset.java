package cz.muni.fi.gag.web.entity;

import javax.persistence.Entity;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
public class FingerSensorOffset extends SensorOffset {
    private FingerPosition position;
}