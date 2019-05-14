package cz.muni.fi.gag.web.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author Vojtech Prusa
 *
 */
@Entity
@Table(name = "Users")
public class User extends AbstractEntity {

    @NotNull
    @Column(nullable = false, unique = true)
    private String thirdPartyId;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private UserRole role;

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole type) {
        this.role = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((thirdPartyId == null) ? 0 : thirdPartyId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        return getThirdPartyId() != null && getThirdPartyId().equals(other.getThirdPartyId());
    }
}