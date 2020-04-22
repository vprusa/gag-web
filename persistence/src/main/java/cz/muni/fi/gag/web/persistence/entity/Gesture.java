package cz.muni.fi.gag.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author Vojtech Prusa
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

    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean isFiltered;

    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean isActive;

    @OneToMany(mappedBy = "gesture", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE/*, orphanRemoval = true*/)
    @JsonIgnoreProperties({"gesture"})
    private List<DataLine> data = new ArrayList<>();

    public Boolean getActive() { return isActive; }

    public void setActive(Boolean active) { isActive = active; }

    public Boolean getFiltered() {
        return isFiltered;
    }

    public void setFiltered(Boolean filtered) {
        isFiltered = filtered;
    }

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

    public void addDataLine(DataLine dl) {
        getData().add(dl);
    }

    @Override
    public String toString() {
        return "Gesture{" +
                "dateCreated=" + (dateCreated != null ? dateCreated : "null") +
                ", userAlias='" + (userAlias != null ? userAlias : "null") + '\'' +
                ", user=" + (user != null ? user : "null") +
                ", isFiltered=" + (isFiltered != null ? isFiltered : "null") +
                '}';
    }

}