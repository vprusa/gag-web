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
    private SensorFingerPosition position;

    /**
     * Getter
     *
     * @return position instance
     */
    public SensorFingerPosition getPosition() {
        return position;
    }

    /**
     * Setter
     * 
     * @param position instance
     */
    public void setPosition(SensorFingerPosition position) {
        this.position = position;
    }

}