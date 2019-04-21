package cz.muni.fi.gag.app.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class HandDevice {
    
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected long id;
	
	@NotNull
	@Column(nullable = false, unique = true)
	private String deviceId;

	@ManyToOne
	@NotNull
    private User user;

    @OneToMany(mappedBy = "device", orphanRemoval = true)
    private List<SensorOffset> offsets = new ArrayList<>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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