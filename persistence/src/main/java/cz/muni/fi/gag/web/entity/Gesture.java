package cz.muni.fi.gag.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.*;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
@Table(name = "gesture")
public class Gesture extends GenericEntity {

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

    @OneToMany(mappedBy = "gesture", fetch = FetchType.EAGER, cascade=CascadeType.REMOVE/*, orphanRemoval = true*/)
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