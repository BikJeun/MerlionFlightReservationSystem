/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class PartnerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerID;
    @Column(nullable = false, length = 64)
    private String name;
    @Column(nullable = false, unique = true, length = 16)
    private String username;
    @Column(nullable = false, unique = true, length = 16)
    private String password;
    
    @OneToMany(mappedBy = "partner")
    private ArrayList<ReservationEntity> reservations;
    //should there be a customer arraylist too??

    public PartnerEntity() {
    }

    public PartnerEntity(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public PartnerEntity(String name, String username, String password, ArrayList<ReservationEntity> reservations) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.reservations = reservations;
    }
    
    public Long getPartnerID() {
        return partnerID;
    }

    public void setPartnerID(Long partnerID) {
        this.partnerID = partnerID;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<ReservationEntity> getReservations() {
        return reservations;
    }

    public void setReservations(ArrayList<ReservationEntity> reservations) {
        this.reservations = reservations;
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (partnerID != null ? partnerID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the partnerID fields are not set
        if (!(object instanceof PartnerEntity)) {
            return false;
        }
        PartnerEntity other = (PartnerEntity) object;
        if ((this.partnerID == null && other.partnerID != null) || (this.partnerID != null && !this.partnerID.equals(other.partnerID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PartnerEntity[ id=" + partnerID + " ]";
    }

    
    
}
