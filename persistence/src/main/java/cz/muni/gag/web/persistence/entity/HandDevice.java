package cz.gag.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.gag.web.persistence.validation.ValidHandOffsets;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
@Table(name = "HandDevice")
public class HandDevice extends GenericEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String deviceId;

    @ManyToOne
    @NotNull
    private User user;

    @OneToMany(mappedBy = "device", orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({ "device" })
    @ValidHandOffsets
    private List<SensorOffset> offsets = new ArrayList<>();

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<SensorOffset> getOffsets() {
        return Collections.unmodifiableList(offsets);
    }

    public void setOffsets(List<SensorOffset> offsets) {
        this.offsets = offsets;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HandDevice)) {
            return false;
        }
        final HandDevice other = (HandDevice) obj;
        return getDeviceId() != null && getDeviceId().equals(other.getDeviceId());
    }
}