/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class FlightScheduleEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long FlightScheduleID;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date departureDateTime;
    
    @Column(nullable = false)
    @Min(1)
    @Max(24)
    private int duration;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FlightSchedulePlanEntity flightSchedulePlan;
    
    @OneToMany(mappedBy = "flightSchedule")
    private List<SeatInventoryEntity> seatInventory;
    
    @OneToMany(mappedBy = "flightSchedule")
    private List<ReservationEntity> reservations;

    public FlightScheduleEntity() {
        reservations = new ArrayList<>();
        seatInventory = new ArrayList<>();
    }

    public FlightScheduleEntity(Date departureDateTime, int duration) {
        this();
        this.departureDateTime = departureDateTime;
        this.duration = duration;
    }

    public Date getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(Date departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<SeatInventoryEntity> getSeatInventory() {
        return seatInventory;
    }

    public void setSeatInventory(List<SeatInventoryEntity> seatInventory) {
        this.seatInventory = seatInventory;
    }

    public List<ReservationEntity> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationEntity> reservations) {
        this.reservations = reservations;
    }

    public Long getFlightScheduleID() {
        return FlightScheduleID;
    }

    public void setFlightScheduleID(Long FlightScheduleID) {
        this.FlightScheduleID = FlightScheduleID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (FlightScheduleID != null ? FlightScheduleID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the FlightScheduleID fields are not set
         // EDIT (BY JUNHAO): Modified to account for when flightScheduleID not set (used in sessionbean)
        if (!(object instanceof FlightScheduleEntity)) {
            return false;
        }
        FlightScheduleEntity other = (FlightScheduleEntity) object;
        if ((this.FlightScheduleID == null && other.FlightScheduleID != null) || (this.FlightScheduleID != null && !this.FlightScheduleID.equals(other.FlightScheduleID)) || (this.FlightScheduleID == null && other.FlightScheduleID == null)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightScheduleEntity[ id=" + FlightScheduleID + " ]";
    }

    /**
     * @return the flightSchedulePlan
     */
    public FlightSchedulePlanEntity getFlightSchedulePlan() {
        return flightSchedulePlan;
    }

    /**
     * @param flightSchedulePlan the flightSchedulePlan to set
     */
    public void setFlightSchedulePlan(FlightSchedulePlanEntity flightSchedulePlan) {
        this.flightSchedulePlan = flightSchedulePlan;
    }
    
}
