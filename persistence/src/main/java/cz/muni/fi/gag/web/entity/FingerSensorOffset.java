package cz.muni.fi.gag.web.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
@Table(name = "fingerSensorOffset")
public class FingerSensorOffset extends SensorOffset {
    private FingerPosition position;
}