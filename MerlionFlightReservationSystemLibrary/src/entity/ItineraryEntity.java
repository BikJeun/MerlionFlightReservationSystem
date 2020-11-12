/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Ooi Jun Hao
 */
@Entity
public class ItineraryEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itineraryId;
    
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 1, max = 32)
    private String creditCardNumber;
    
    @Column(nullable = false, length = 3)
    @NotNull
    @Size(min = 3, max = 3)
    private String cvv;
  
    @ManyToOne(optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(nullable = false)
    private UserEntity user;
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "itinerary", cascade = CascadeType.DETACH)
    private List<ReservationEntity> reservations;

    public ItineraryEntity() {
        this.reservations = new ArrayList<>();
    }

    public ItineraryEntity(String creditCardNumber, String cvv) {
        this();
        this.creditCardNumber = creditCardNumber;
        this.cvv = cvv;
    }
    
    public Long getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(Long itineraryId) {
        this.itineraryId = itineraryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (itineraryId != null ? itineraryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the itineraryId fields are not set
        if (!(object instanceof ItineraryEntity)) {
            return false;
        }
        ItineraryEntity other = (ItineraryEntity) object;
        if ((this.itineraryId == null && other.itineraryId != null) || (this.itineraryId != null && !this.itineraryId.equals(other.itineraryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ItineraryEntity[ id=" + itineraryId + " ]";
    }

    /**
     * @return the creditCardNumber
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    /**
     * @param creditCardNumber the creditCardNumber to set
     */
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    /**
     * @return the cvv
     */
    public String getCvv() {
        return cvv;
    }

    /**
     * @param cvv the cvv to set
     */
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    /**
     * @return the user
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /**
     * @return the reservations
     */
    public List<ReservationEntity> getReservations() {
        return reservations;
    }

    /**
     * @param reservations the reservations to set
     */
    public void setReservations(List<ReservationEntity> reservations) {
        this.reservations = reservations;
    }
    
}
