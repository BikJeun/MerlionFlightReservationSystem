/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
    @Column(nullable = false, length = 32)
    private String creditCardNumber;
    @Column(nullable = false, length = 3)
    private String cvv;
    
    @OneToMany(fetch = FetchType.EAGER)
    private List<PassengerEntity> passenger;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private UserEntity user;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FlightScheduleEntity flightSchedule;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FareEntity fare;
      
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CabinClassEntity cabinClass;

    public ReservationEntity() {
    }

    public ReservationEntity(String creditCardNumber, String cvv, List<PassengerEntity> passenger, UserEntity user, FlightScheduleEntity flightSchedule, FareEntity fare, CabinClassEntity cabinClass) {
        this.creditCardNumber = creditCardNumber;
        this.cvv = cvv;
        this.passenger = passenger;
        this.user = user;
        this.flightSchedule = flightSchedule;
        this.fare = fare;
        this.cabinClass = cabinClass;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
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
     * @return the fare
     */
    public FareEntity getFare() {
        return fare;
    }

    /**
     * @param fare the fare to set
     */
    public void setFare(FareEntity fare) {
        this.fare = fare;
    }

    /**
     * @return the cabinClass
     */
    public CabinClassEntity getCabinClass() {
        return cabinClass;
    }

    /**
     * @param cabinClass the cabinClass to set
     */
    public void setCabinClass(CabinClassEntity cabinClass) {
        this.cabinClass = cabinClass;
    }
    
}
