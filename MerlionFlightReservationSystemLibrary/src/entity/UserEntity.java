/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Ooi Jun Hao
 */
@Entity
@Inheritance(strategy= InheritanceType.JOINED)
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long UserID;
    
    @Column(nullable = false, unique = true, length = 16)
    @NotNull
    @Size(min = 1, max = 16)
    private String username;
    
    @Column(nullable = false, length = 16)
    @NotNull
    @Size(min = 1, max = 16)
    private String password;
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<ItineraryEntity> itineraries;    

    public UserEntity() {
        this.itineraries = new ArrayList<>();
    }
    
    public UserEntity(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    public Long getUserID() {
        return UserID;
    }

    public void setUserID(Long UserID) {
        this.UserID = UserID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (UserID != null ? UserID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the UserID fields are not set
        if (!(object instanceof UserEntity)) {
            return false;
        }
        UserEntity other = (UserEntity) object;
        if ((this.UserID == null && other.UserID != null) || (this.UserID != null && !this.UserID.equals(other.UserID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.UserEntity[ id=" + UserID + " ]";
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the itineraries
     */
    public List<ItineraryEntity> getItineraries() {
        return itineraries;
    }

    /**
     * @param itineraries the itineraries to set
     */
    public void setItineraries(List<ItineraryEntity> itineraries) {
        this.itineraries = itineraries;
    }
    
}
