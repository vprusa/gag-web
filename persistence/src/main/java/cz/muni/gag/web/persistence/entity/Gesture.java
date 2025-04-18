package cz.gag.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.*;

/**
 * @author Vojtech Prusa
 */
@Entity
@Table(name = "Gesture")
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

    // TODO ...
    // Do not forget that this is a threshold for absolute distance of 2 quaternions
    @Column(columnDefinition = "float(5) not null default 0.2")
    private float shouldMatch;

    @Column(columnDefinition = "TEXT")
    private String exec;


    @Column(columnDefinition = "float(2) not null default 1.0")
    private float delay;

    //    @JsonIgnore
    @OneToMany(mappedBy = "gesture", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE/*, orphanRemoval = true*/)
    @JsonIgnoreProperties({"gesture"})
    private List<DataLine> data = new ArrayList<>();

    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

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

    public float getShouldMatch() {
        return shouldMatch;
    }

    public void setShouldMatch(float shouldMatch) {
        this.shouldMatch = shouldMatch;
    }

    public String getExec() {
        return exec;
    }

    public void setExec(String exec) {
        this.exec = exec;
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
                ", isActive=" + (isActive != null ? isActive : "null") +
                ", shouldMatch=" + (shouldMatch) +
                ", delay=" + (delay) +
                ", exec=" + (exec != null ? exec : "null") +
                '}';
    }

}