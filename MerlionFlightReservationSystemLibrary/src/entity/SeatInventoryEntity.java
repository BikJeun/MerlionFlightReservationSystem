/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author Mitsuki
 */
@Entity
public class SeatInventoryEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatInventoryID;
    @Column(nullable = false)
    private int available;
    @Column(nullable = false)
    private int reserved;
    @Column(nullable = false)
    private int balance;
    
    @OneToOne
    private CabinClassEntity cabin;
    @ManyToOne
    private FlightScheduleEntity flightSchedule;

    public SeatInventoryEntity() {
    }

    public SeatInventoryEntity(Long seatInventoryID, int available, int reserved, int balance, CabinClassEntity cabin, FlightScheduleEntity flightSchedule) {
        this.seatInventoryID = seatInventoryID;
        this.available = available;
        this.reserved = reserved;
        this.balance = balance;
        this.cabin = cabin;
        this.flightSchedule = flightSchedule;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public CabinClassEntity getCabin() {
        return cabin;
    }

    public void setCabin(CabinClassEntity cabin) {
        this.cabin = cabin;
    }

    public FlightScheduleEntity getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(FlightScheduleEntity flightSchedule) {
        this.flightSchedule = flightSchedule;
    }
    
    

    public Long getSeatInventoryID() {
        return seatInventoryID;
    }

    public void setSeatInventoryID(Long seatInventoryID) {
        this.seatInventoryID = seatInventoryID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (seatInventoryID != null ? seatInventoryID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the seatInventoryID fields are not set
        if (!(object instanceof SeatInventoryEntity)) {
            return false;
        }
        SeatInventoryEntity other = (SeatInventoryEntity) object;
        if ((this.seatInventoryID == null && other.seatInventoryID != null) || (this.seatInventoryID != null && !this.seatInventoryID.equals(other.seatInventoryID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.SeatInventoryEntity[ id=" + seatInventoryID + " ]";
    }
    
}
