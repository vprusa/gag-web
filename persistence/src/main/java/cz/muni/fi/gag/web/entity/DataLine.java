package cz.muni.fi.gag.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Vojtech Prusa
 * 
 * {@link FingerDataLine}
 * {@link WristDataLine}
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties(value= {"gesture"})
public /*abstract */class DataLine extends AbstractEntity implements Serializable {

    @NotNull
    @PastOrPresent
    protected Date timestamp;

    // https://stackoverflow.com/questions/26957554/
    // jsonmappingexception-could-not-initialize-proxy-no-session
    @ManyToOne(fetch = FetchType.LAZY)
    @Null // todo @NotNull .. fix tests?
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

    public void setGesture(Gesture gesture) {
        this.gesture = gesture;
    }

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