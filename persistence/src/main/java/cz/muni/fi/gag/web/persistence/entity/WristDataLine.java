package cz.muni.fi.gag.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
public class WristDataLine extends FingerDataLine {

    @JsonProperty("mX")
    private short magX;

    @JsonProperty("mY")
    private short magY;

    @JsonProperty("mZ")
    private short magZ;

    // field SensorFingerPosition should not be used here - > Transient
    @Transient
    @Override
    public Sensor getPosition() {
        return super.getPosition();
    }



    /**
     * Getter
     *
     * @return magX instance
     */
    public short getMagX() {
        return magX;
    }

    /**
     * Setter
     * 
     * @param magX instance
     */
    public void setMagX(short magX) {
        this.magX = magX;
    }

    /**
     * Getter
     *
     * @return magY instance
     */
    public short getMagY() {
        return magY;
    }

    /**
     * Setter
     * 
     * @param magY instance
     */
    public void setMagY(short magY) {
        this.magY = magY;
    }

    /**
     * Getter
     *
     * @return magZ instance
     */
    public short getMagZ() {
        return magZ;
    }

    /**
     * Setter
     * 
     * @param magZ instance
     */
    public void setMagZ(short magZ) {
        this.magZ = magZ;
    }

    @Override
    public String toString() {
        return super.toString() + " WristDataLine [magX=" + magX + ", magY=" + magY + ", magZ=" + magZ + "]";
    }


    public WristDataLine deepCopy() {
        WristDataLine copy = new WristDataLine();

        // Copy timestamp
        copy.timestamp = (this.timestamp != null) ? new Date(this.timestamp.getTime()) : null;

        // Copy gesture
        copy.gesture = this.gesture;

        // Copy position
        copy.setPosition(this.getPosition());

        // Copy hand position
        copy.setHandPosition(this.getHandPosition());

        copy.setAccX(getAccX());
        copy.setAccY(getAccY());
        copy.setAccZ(getAccZ());
        copy.setQuatX(this.getQuatX());
        copy.setQuatY(this.getQuatY());
        copy.setQuatZ(this.getQuatZ());
        copy.setQuatA(this.getQuatA());
        copy.setMagX(this.getMagX());
        copy.setMagY(this.getMagY());
        copy.setMagZ(this.getMagZ());

        return copy;
    }

}