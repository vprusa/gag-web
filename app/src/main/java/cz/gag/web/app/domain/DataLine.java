package cz.muni.fi.gag.app.web.domain;

import java.time.LocalTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class DataLine {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected long id;
	
	@NotNull
    private LocalTime timestamp;
    
    @ManyToOne
    @NotNull
    private Gesture gesture;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalTime timestamp) {
		this.timestamp = timestamp;
	}

	public Gesture getGesture() {
		return gesture;
	}

	public void setGesture(Gesture gesture) {
		this.gesture = gesture;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gesture == null) ? 0 : gesture.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
        if (!(obj instanceof DataLine)) {
            return false;
        }
        final DataLine other = (DataLine) obj;
		return getGesture() != null && getGesture().equals(other.getGesture())
				     && getTimestamp() != null && getGesture().equals(other.getGesture());
	}
   
}