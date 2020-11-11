/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package entity;

import enumeration.CabinClassTypeEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class ReservationEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationID;
        
    /* Fare codes start with a
    letter that denotes the booking class. Other letters or numbers may follow. Typically a fare
    basis will be 3 to 7 characters long.*/
    @Column(nullable = false, length = 7)
    @NotNull
    @Size(min = 2, max = 7)
    private String fareBasisCode;
    
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal fareAmount;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CabinClassTypeEnum cabinClassType;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ItineraryEntity itinerary;
    
    @OneToMany(fetch = FetchType.EAGER)
    private List<PassengerEntity> passenger; 
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FlightScheduleEntity flightSchedule;
    
     
    public ReservationEntity() {
        passenger = new ArrayList<>(); 
    }

    public ReservationEntity(String fareBasisCode, BigDecimal fareAmount, CabinClassTypeEnum cabinClassType) {
        this();
        this.fareBasisCode = fareBasisCode;
        this.fareAmount = fareAmount;
        this.cabinClassType = cabinClassType;
    }

    public List<PassengerEntity> getPassenger() {
        return passenger;
    }

    public void setPassenger(List<PassengerEntity> passenger) {
        this.passenger = passenger;
    }

    public FlightScheduleEntity getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(FlightScheduleEntity flightSchedule) {
        this.flightSchedule = flightSchedule;
    }  
        
    public Long getReservationID() {
        return reservationID;
    }
    
    public void setReservationID(Long reservationID) {
        this.reservationID = reservationID;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationID != null ? reservationID.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationID fields are not set
        if (!(object instanceof ReservationEntity)) {
            return false;
        }
        ReservationEntity other = (ReservationEntity) object;
        if ((this.reservationID == null && other.reservationID != null) || (this.reservationID != null && !this.reservationID.equals(other.reservationID))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "entity.ReservationEntity[ id=" + reservationID + " ]";
    }

    /**
     * @return the fareBasisCode
     */
    public String getFareBasisCode() {
        return fareBasisCode;
    }

    /**
     * @param fareBasisCode the fareBasisCode to set
     */
    public void setFareBasisCode(String fareBasisCode) {
        this.fareBasisCode = fareBasisCode;
    }

    /**
     * @return the fareAmount
     */
    public BigDecimal getFareAmount() {
        return fareAmount;
    }

    /**
     * @param fareAmount the fareAmount to set
     */
    public void setFareAmount(BigDecimal fareAmount) {
        this.fareAmount = fareAmount;
    }

    /**
     * @return the cabinClassType
     */
    public CabinClassTypeEnum getCabinClassType() {
        return cabinClassType;
    }

    /**
     * @param cabinClassType the cabinClassType to set
     */
    public void setCabinClassType(CabinClassTypeEnum cabinClassType) {
        this.cabinClassType = cabinClassType;
    }

    /**
     * @return the itinerary
     */
    public ItineraryEntity getItinerary() {
        return itinerary;
    }

    /**
     * @param itinerary the itinerary to set
     */
    public void setItinerary(ItineraryEntity itinerary) {
        this.itinerary = itinerary;
    }

}