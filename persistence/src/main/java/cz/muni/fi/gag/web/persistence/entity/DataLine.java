package cz.muni.fi.gag.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureIterator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Vojtech Prusa
 * 
 * {@link FingerDataLine}
 * {@link WristDataLine}
 * {@link DataLineGestureIterator}
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
    //@Column(name = "timestamp", columnDefinition="DATETIME(6)")
    //@Temporal(TemporalType.TIMESTAMP)
    protected Date timestamp;

    // https://stackoverflow.com/questions/26957554/
    // jsonmappingexception-could-not-initialize-proxy-no-session
    // TODO fix find by reference?
    @ManyToOne(fetch = FetchType.EAGER)
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

    public void setGesture(Gesture gesture) {
        //setGesture(new Long(gesture));
        this.gesture = gesture;
    }

    private Sensor position;

    @Enumerated(EnumType.ORDINAL)
    @JsonProperty("p")
    public Sensor getPosition() {
        return position;
    }

    public void setPosition(Sensor position) {
        this.position = position;
    }

    @Enumerated(EnumType.ORDINAL)
    @JsonProperty("h")
    private Hand hand;

    public Hand getHandPosition() {
        return hand;
    }

    public void setHandPosition(Hand hand) {
        this.hand = this.hand;
    }

    public DataLine deepCopy() {
        DataLine copy = new DataLine();

        // Copy timestamp
        copy.timestamp = (this.timestamp != null) ? new Date(this.timestamp.getTime()) : null;

        // Copy gesture
        copy.gesture = this.gesture;

        // Copy position
        copy.position = this.position;

        // Copy hand position
        copy.hand = this.hand;

        return copy;
    }

    @Override
    public String toString() {
        return "DataLine [id=" + id + ", timestamp=" + timestamp + " ( " + timestamp.getTime() + " ), gesture=" + gesture
                + ", hand=" + hand + ", position=" + position + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((gesture == null) ? 0 : gesture.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((hand == null) ? 0 : hand.hashCode());
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
        if (position != other.getPosition()) {
            return false;
        }
        if (hand != other.getHandPosition()) {
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