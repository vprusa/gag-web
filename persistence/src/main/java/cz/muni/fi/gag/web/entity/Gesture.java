package cz.muni.fi.gag.web.entity;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
@Table(name = "gesture")
public class Gesture extends AbstractEntity {

    @NotNull
    private Date dateCreated;

    @NotNull
    private String userAlias;

    @ManyToOne
    @NotNull
    private User user;

    @OneToMany(mappedBy = "gesture", orphanRemoval = false)
    private List<DataLine> data = new ArrayList<>();

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
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

    public List getData() {
        return Collections.unmodifiableList(data);
    }

    public void setData(List data) {
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
        return getDateCreated() != null && getDateCreated().equals(other.getDateCreated()) && getUser() != null
                && getUser().equals(other.getUser());
    }
}