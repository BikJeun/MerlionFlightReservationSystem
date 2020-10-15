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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Mitsuki
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
    private ArrayList<PassengerEntity> passenger;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CustomerEntity customer;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FlightScheduleEntity flightSchedule;
    @ManyToOne(optional = true)
    @JoinColumn(nullable = true)
    private PartnerEntity partner;

    public ReservationEntity() {
    }

    public ReservationEntity(String creditCardNumber, String cvv, ArrayList<PassengerEntity> passenger, CustomerEntity customer, FlightScheduleEntity flightSchedule, PartnerEntity partner) {
        this.creditCardNumber = creditCardNumber;
        this.cvv = cvv;
        this.passenger = passenger;
        this.customer = customer;
        this.flightSchedule = flightSchedule;
        this.partner = partner;
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

    public ArrayList<PassengerEntity> getPassenger() {
        return passenger;
    }

    public void setPassenger(ArrayList<PassengerEntity> passenger) {
        this.passenger = passenger;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public FlightScheduleEntity getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(FlightScheduleEntity flightSchedule) {
        this.flightSchedule = flightSchedule;
    }

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
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
    
}
