package cz.muni.fi.gag.web.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Vojtech Prusa
 * 
 *         TODO fix like
 *         https://stackoverflow.com/questions/4045511/manytomany-in-an-abstract-mappedsuperclass
 *         ?
 * 
 * {@link DataLine}
 * {@link FingerDataLine}
 * {@link WristDataLine}
 * {@link User}
 * {@link SensorOffset}
 * {@link FingerSensorOffset}
 * {@link WristSensorOffset}
 * {@link Gesture}
 * {@link HandDevice}
 * 
 */
//@ApplicationScoped
//@Stateless
@MappedSuperclass
public /*abstract*/ class AbstractEntity extends AbstractBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@JsonProperty("i")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}