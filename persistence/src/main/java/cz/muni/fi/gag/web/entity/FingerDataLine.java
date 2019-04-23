package cz.muni.fi.gag.web.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;

import toxi.geom.Quaternion;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
public class FingerDataLine extends DataLine {

    // TODO wrapper/converter?
    private Quaternion quat;

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
     * @return quat instance
     */
    public Quaternion getQuat() {
        return quat;
    }

    /**
     * Setter
     * 
     * @param quat instance
     */
    public void setQuat(Quaternion quat) {
        this.quat = quat;
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

}