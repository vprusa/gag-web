package cz.muni.fi.gag.web.entity;

import java.util.logging.Logger;
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
public /*abstract*/ class AbstractBaseEntity {

    @Transient
    protected static final Logger log = Logger.getLogger(AbstractBaseEntity.class.getSimpleName());

}