package cz.muni.fi.gag.web.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
@Table(name = "fingerSensorOffset")
@DiscriminatorValue("Finger")
public class FingerSensorOffset extends SensorOffset {
    
    @Enumerated(EnumType.ORDINAL)
    private Sensor position;

    /**
     * Getter
     *
     * @return position instance
     */
    public Sensor getPosition() {
        return position;
    }

    /**
     * Setter
     * 
     * @param position instance
     */
    public void setPosition(Sensor position) {
        this.position = position;
    }

}