package cz.muni.fi.gag.web.entity;

import javax.persistence.Entity;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
public class WristDataLine extends FingerDataLine {

    private short magX;

    private short magY;

    private short magZ;

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
        return "WristDataLine [magX=" + magX + ", magY=" + magY + ", magZ=" + magZ + "] "
                + super.toString();
    }

}