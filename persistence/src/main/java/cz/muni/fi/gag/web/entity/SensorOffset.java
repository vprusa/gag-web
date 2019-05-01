package cz.muni.fi.gag.web.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * @author Vojtech Prusa
 *
 */
//@MappedSuperclass
@Entity
public abstract class SensorOffset extends AbstractEntity {

    /*
     * TODO are there any validation constraints we might apply (value in some
     * range...?)
     */
    private short x;

    private short y;

    private short z;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "offsets")
    private HandDevice device;

    @Enumerated(EnumType.ORDINAL)
    private SensorType sensorType;

    public short getX() {
        return x;
    }

    public void setX(short x) {
        this.x = x;
    }

    public short getY() {
        return y;
    }

    public void setY(short y) {
        this.y = y;
    }

    public short getZ() {
        return z;
    }

    public void setZ(short z) {
        this.z = z;
    }

    public HandDevice getDevice() {
        return device;
    }

    public void setDevice(HandDevice device) {
        this.device = device;
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((device == null) ? 0 : device.hashCode());
        result = prime * result + ((sensorType == null) ? 0 : sensorType.hashCode());
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SensorOffset)) {
            return false;
        }
        final SensorOffset other = (SensorOffset) obj;
        return getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ() && getDevice() != null
                && getDevice().equals(other.getDevice()) && getSensorType() != null
                && getSensorType().equals(other.getSensorType());
    }
}