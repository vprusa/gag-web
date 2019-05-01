package cz.muni.fi.gag.web.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Vojtech Prusa
 * 
 * TODO fix like https://stackoverflow.com/questions/4045511/manytomany-in-an-abstract-mappedsuperclass ?
 * 
 * @DataLine
 * @FingerDataLine
 * @WristDataLine
 * @User
 * @SensorOffset
 * @FingerSensorOffset
 * @WristSensorOffset
 * @Gesture
 * @HandDevice
 * 
 */
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
}