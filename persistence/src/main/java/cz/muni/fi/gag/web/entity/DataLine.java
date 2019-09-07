package cz.muni.fi.gag.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

/**
 * @author Vojtech Prusa
 * 
 * {@link FingerDataLine}
 * {@link WristDataLine}
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties(allowSetters = true, value = {"gesture"})
//@JsonIgnoreProperties(allowSetters = true, value = {"g"})
//@ApplicationScoped
public /*abstract */class DataLine extends GenericEntity implements Serializable {

    @NotNull
    @PastOrPresent
    @JsonProperty("t")
    protected Date timestamp;

    // https://stackoverflow.com/questions/26957554/
    // jsonmappingexception-could-not-initialize-proxy-no-session
    // TODO fix find by reference?
    @ManyToOne(fetch = FetchType.LAZY)
    //@Null // todo @NotNull .. fix tests?
    //@JsonProperty("g")
    protected Gesture gesture;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Gesture getGesture() {
        return gesture;
    }
/*
    @Transient
    @Inject
    protected UserDao userDao;

    private EntityManager getEM(){
        return userDao.getEm();
    }

    public void setGesture(Object gesture) {
        log.info(gesture.toString());
        log.info(gesture.getClass().toString());
        if(gesture instanceof Gesture) {
            this.gesture = (Gesture)gesture;
        } else if(gesture instanceof Long) {
            try {
                EntityManager em = this.getEM();
                if(em == null){
                    log.info("em == null");
                } else {
                    log.info(em.toString());
                    this.gesture = em.getReference(Gesture.class, gesture);
                }
            } catch(Exception e){
                log.info(e.getMessage());
                e.printStackTrace();
            }
        } else {
            // TODO throw exception or ignore?
        }

        //this.gesture = gesture;
    }*/

    public void setGesture(Gesture gesture) {
        //setGesture(new Long(gesture));
        this.gesture = gesture;
    }
/*
    public void setGesture(int gesture) {
        setGesture(new Long(gesture));
    }
*/
    @Override
    public String toString() {
        return "DataLine [id=" + id + ", timestamp=" + timestamp + ", gesture=" + gesture + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((gesture == null) ? 0 : gesture.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DataLine || obj instanceof WristDataLine || obj instanceof FingerDataLine)) {
            return false;
        }
        DataLine other = (DataLine) obj;
        if (gesture == null) {
            if (other.gesture != null) {
                return false;
            }
        } else if (!gesture.equals(other.gesture)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (timestamp == null) {
            if (other.timestamp != null) {
                return false;
            }
        } else if (!timestamp.equals(other.timestamp)) {
            return false;
        }
        return true;
    }

    /**
     * @author Vojtech Prusa
     *
     */
    public static class Aggregate<DataLineEx extends DataLine> {
        
        DataLineEx data;
        
        public Aggregate(DataLineEx data) {
            super();
            this.data = data;
        }

        /**
         * Getter
         *
         * @return data instance
         */
        public DataLineEx getData() {
            return data;
        }

    }

}