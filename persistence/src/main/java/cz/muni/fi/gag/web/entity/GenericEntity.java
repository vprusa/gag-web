package cz.muni.fi.gag.web.entity;

import java.util.logging.Logger;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

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
public /*abstract*/ class GenericEntity extends BaseEntity {

    @Transient
    protected static final Logger log = Logger.getLogger(GenericEntity.class.getSimpleName());

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