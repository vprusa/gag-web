package cz.muni.fi.gag.web.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
@Table(name = "wristSensorOffset")
@DiscriminatorValue("Wrist")
public class WristSensorOffset extends SensorOffset {
}