/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import enumeration.ScheduleTypeEnum;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
    @Column(nullable = false, length = 32)
    private String flightNum;
    @Enumerated(EnumType.STRING)
    private ScheduleTypeEnum scheduleType;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date departureDateTime;
    @Column(nullable = false)
    @Min(1)
    @Max(24)
    private int duration;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date arrivalDateTime;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date recurrentEndDate;
    
    @ManyToMany
    private ArrayList<FlightSchedulePlanEntity> flightSchedulePlan;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FlightEntity flight;
    @OneToMany(mappedBy = "flightSchedule")
    private ArrayList<SeatInventoryEntity> seatInventory;
    @OneToMany(mappedBy = "flightSchedule")
    private ArrayList<ReservationEntity> reservations;

    public FlightScheduleEntity() {
        flightSchedulePlan = new ArrayList<>();
        seatInventory = new ArrayList<>();
    }

    public FlightScheduleEntity(String flightNum, ScheduleTypeEnum scheduleType, Date departureDateTime, int duration, Date arrivalDateTime, Date recurrentEndDate, ArrayList<FlightSchedulePlanEntity> flightSchedulePlan, FlightEntity flight, ArrayList<SeatInventoryEntity> seatInventory, ArrayList<ReservationEntity> reservations) {
        this();
        this.flightNum = flightNum;
        this.scheduleType = scheduleType;
        this.departureDateTime = departureDateTime;
        this.duration = duration;
        this.arrivalDateTime = arrivalDateTime;
        this.recurrentEndDate = recurrentEndDate;
        this.flightSchedulePlan = flightSchedulePlan;
        this.flight = flight;
        this.seatInventory = seatInventory;
        this.reservations = reservations;
    }

    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    public ScheduleTypeEnum getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleTypeEnum scheduleType) {
        this.scheduleType = scheduleType;
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

    public Date getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(Date arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public Date getRecurrentEndDate() {
        return recurrentEndDate;
    }

    public void setRecurrentEndDate(Date recurrentEndDate) {
        this.recurrentEndDate = recurrentEndDate;
    }

    public ArrayList<FlightSchedulePlanEntity> getFlightSchedulePlan() {
        return flightSchedulePlan;
    }

    public void setFlightSchedulePlan(ArrayList<FlightSchedulePlanEntity> flightSchedulePlan) {
        this.flightSchedulePlan = flightSchedulePlan;
    }

    public FlightEntity getFlight() {
        return flight;
    }

    public void setFlight(FlightEntity flight) {
        this.flight = flight;
    }

    public ArrayList<SeatInventoryEntity> getSeatInventory() {
        return seatInventory;
    }

    public void setSeatInventory(ArrayList<SeatInventoryEntity> seatInventory) {
        this.seatInventory = seatInventory;
    }

    public ArrayList<ReservationEntity> getReservations() {
        return reservations;
    }

    public void setReservations(ArrayList<ReservationEntity> reservations) {
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
        if (!(object instanceof FlightScheduleEntity)) {
            return false;
        }
        FlightScheduleEntity other = (FlightScheduleEntity) object;
        if ((this.FlightScheduleID == null && other.FlightScheduleID != null) || (this.FlightScheduleID != null && !this.FlightScheduleID.equals(other.FlightScheduleID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightScheduleEntity[ id=" + FlightScheduleID + " ]";
    }
    
}
