package cz.muni.fi.gag.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
@Table(name = "gesture")
public class Gesture extends AbstractEntity {

    @NotNull
    @PastOrPresent
    private Date dateCreated;

    @NotBlank
    private String userAlias;

    @ManyToOne
    @NotNull
    private User user;

    @Column(columnDefinition="tinyint(1) default 0")
    private Boolean isFiltered;

    @OneToMany(mappedBy = "gesture", orphanRemoval = false, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"gesture"})
    private List<DataLine> data = new ArrayList<>();

    public Boolean getFiltered() { return isFiltered; }

    public void setFiltered(Boolean filtered) { isFiltered = filtered; }

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

    /*
    // Eclipse generated
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
    */

    // IDEA generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gesture)) return false;
        Gesture gesture = (Gesture) o;
        return Objects.equals(getDateCreated(), gesture.getDateCreated()) &&
                Objects.equals(getUserAlias(), gesture.getUserAlias()) &&
                Objects.equals(getUser(), gesture.getUser()) &&
                Objects.equals(isFiltered, gesture.isFiltered) &&
                Objects.equals(getData(), gesture.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDateCreated(), getUserAlias(), getUser(), isFiltered, getData());
    }
}