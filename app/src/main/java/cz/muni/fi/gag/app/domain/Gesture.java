package cz.muni.fi.gag.app.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class Gesture {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected long id;
	
	@NotNull
    private LocalDateTime dateCreated;

    @NotNull
    private String userAlias;

    @ManyToOne
    @NotNull
    private User user;
    
    @OneToMany(mappedBy = "gesture", orphanRemoval = true)
    private List<DataLine> data = new ArrayList<>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getUserAlias() {
		return userAlias;
	}

	public void setUserAlias(String userAlias) {
		this.userAlias = userAlias;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<DataLine> getData() {
		return Collections.unmodifiableList(data);
	}

	public void setData(List<DataLine> data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateCreated == null) ? 0 : dateCreated.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
        if (!(obj instanceof Gesture)) {
            return false;
        }
		final Gesture other = (Gesture) obj;
		return getDateCreated() != null &&
				getDateCreated().equals(other.getDateCreated()) &&
				getUser() != null &&
				getUser().equals(other.getUser());
	}
}