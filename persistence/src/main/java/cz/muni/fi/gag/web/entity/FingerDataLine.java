package cz.muni.fi.gag.web.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

//import toxi.geom.Quaternion;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
public class FingerDataLine extends DataLine {

    // TODO wrapper/converter?
    // private Quaternion quat;

    private float quatA;
    private float quatX;
    private float quatY;
    private float quatZ;

    private short accX;

    private short accY;

    private short accZ;

    @Enumerated(EnumType.ORDINAL)
    private FingerPosition position;

    public FingerPosition getPosition() {
        return position;
    }

    public void setPosition(FingerPosition position) {
        this.position = position;
    }

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
    public short getX() {
        return accX;
    }

    /**
     * Setter
     * 
     * @param x instance
     */
    public void setX(short x) {
        this.accX = x;
    }

    /**
     * Getter
     *
     * @return y instance
     */
    public short getY() {
        return accY;
    }

    /**
     * Setter
     * 
     * @param y instance
     */
    public void setY(short y) {
        this.accY = y;
    }

    /**
     * Getter
     *
     * @return z instance
     */
    public short getZ() {
        return accZ;
    }

    /**
     * Setter
     * 
     * @param z instance
     */
    public void setZ(short z) {
        this.accZ = z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + accX;
        result = prime * result + accY;
        result = prime * result + accZ;
        result = prime * result + ((position == null) ? 0 : position.hashCode());
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
        if (position != other.position) {
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
        return "FingerDataLine [quatA=" + quatA + ", quatX=" + quatX + ", quatY=" + quatY + ", quatZ=" + quatZ
                + ", accX=" + accX + ", accY=" + accY + ", accZ=" + accZ + ", position=" + position + "]"
                + super.toString();
    }

}