package cz.muni.fi.gag.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;

/**
 * @author Vojtech Prusa
 *
 * {@link WristDataLine}
 *
 */
@Entity
//@ApplicationScoped
public class FingerDataLine extends DataLine {

    // TODO wrapper/converter?
    // private Quaternion quat;

    @JsonProperty("qA")
    private float quatA;
    @JsonProperty("qX")
    private float quatX;
    @JsonProperty("qY")
    private float quatY;
    @JsonProperty("qZ")
    private float quatZ;

    @JsonProperty("aX")
    private short accX;
    @JsonProperty("aY")
    private short accY;
    @JsonProperty("aZ")
    private short accZ;

    /**
     * Getter
     *
     * @return quatA instance
     */
    public float getQuatA() {
        return quatA;
    }

    /**
     * Setter
     * 
     * @param quatA instance
     */
    public void setQuatA(float quatA) {
        this.quatA = quatA;
    }

    /**
     * Getter
     *
     * @return quatX instance
     */
    public float getQuatX() {
        return quatX;
    }

    /**
     * Setter
     * 
     * @param quatX instance
     */
    public void setQuatX(float quatX) {
        this.quatX = quatX;
    }

    /**
     * Getter
     *
     * @return quatY instance
     */
    public float getQuatY() {
        return quatY;
    }

    /**
     * Setter
     * 
     * @param quatY instance
     */
    public void setQuatY(float quatY) {
        this.quatY = quatY;
    }

    /**
     * Getter
     *
     * @return quatZ instance
     */
    public float getQuatZ() {
        return quatZ;
    }

    /**
     * Setter
     * 
     * @param quatZ instance
     */
    public void setQuatZ(float quatZ) {
        this.quatZ = quatZ;
    }

    /**
     * Getter
     *
     * @return x instance
     */
    public short getAccX() {
        return accX;
    }

    /**
     * Setter
     * 
     * @param x instance
     */
    public void setAccX(short x) {
        this.accX = x;
    }

    /**
     * Getter
     *
     * @return y instance
     */
    public short getAccY() {
        return accY;
    }

    /**
     * Setter
     * 
     * @param y instance
     */
    public void setAccY(short y) {
        this.accY = y;
    }

    /**
     * Getter
     *
     * @return z instance
     */
    public short getAccZ() {
        return accZ;
    }

    /**
     * Setter
     * 
     * @param z instance
     */
    public void setAccZ(short z) {
        this.accZ = z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + accX;
        result = prime * result + accY;
        result = prime * result + accZ;
//        result = prime * result + ((getPosition() == null) ? 0 : getPosition().hashCode());
        result = prime * result + Float.floatToIntBits(quatA);
        result = prime * result + Float.floatToIntBits(quatX);
        result = prime * result + Float.floatToIntBits(quatY);
        result = prime * result + Float.floatToIntBits(quatZ);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof FingerDataLine)) {
            return false;
        }
        FingerDataLine other = (FingerDataLine) obj;
        if (accX != other.accX) {
            return false;
        }
        if (accY != other.accY) {
            return false;
        }
        if (accZ != other.accZ) {
            return false;
        }
        if (Float.floatToIntBits(quatA) != Float.floatToIntBits(other.quatA)) {
            return false;
        }
        if (Float.floatToIntBits(quatX) != Float.floatToIntBits(other.quatX)) {
            return false;
        }
        if (Float.floatToIntBits(quatY) != Float.floatToIntBits(other.quatY)) {
            return false;
        }
        if (Float.floatToIntBits(quatZ) != Float.floatToIntBits(other.quatZ)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " FingerDataLine [quatA=" + quatA + ", quatX=" + quatX + ", quatY=" + quatY + ", quatZ=" + quatZ
                + ", accX=" + accX + ", accY=" + accY + ", accZ=" + accZ + "]";
    }

}