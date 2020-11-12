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
import java.util.List;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class FlightSchedulePlanEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightSchedulePlanID;
    
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min=3, max=32)
    private String flightNum;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ScheduleTypeEnum typeExistingInPlan;
    
    @Column(nullable = false)
    @NotNull
    private boolean disabled;
        
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date recurrentEndDate;
    
    @OneToMany(mappedBy = "flightSchedulePlan",  fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private List<FlightScheduleEntity> flightSchedule;
    
    @ManyToOne(optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(nullable = false)
    private FlightEntity flight;
    
    @OneToMany(mappedBy = "flightSchedulePlan",  fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private List<FareEntity> fares;
    
    @OneToOne(mappedBy = "complementary", cascade = CascadeType.DETACH)
    private FlightSchedulePlanEntity source;
    @OneToOne(cascade = CascadeType.DETACH)
    private FlightSchedulePlanEntity complementary;
    
    public FlightSchedulePlanEntity() {
        flightSchedule = new ArrayList<>();
        fares = new ArrayList<>();
        disabled = false;
    }

    //For non-recurrent schedules
    public FlightSchedulePlanEntity(ScheduleTypeEnum typeExistingInPlan, FlightEntity flight) {
        this();
        this.typeExistingInPlan = typeExistingInPlan;
        this.flight = flight;
        this.flightNum = flight.getFlightNum();
        this.recurrentEndDate = null;
    }

    //For recurrent schedules
    public FlightSchedulePlanEntity(ScheduleTypeEnum typeExistingInPlan, Date recurrentEndDate, FlightEntity flight) {
        this();
        this.typeExistingInPlan = typeExistingInPlan;
        this.recurrentEndDate = recurrentEndDate;
        this.flight = flight;
        this.flightNum = flight.getFlightNum();
    }
    

    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    public List<FlightScheduleEntity> getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(List<FlightScheduleEntity> flightSchedule) {
        this.flightSchedule = flightSchedule;
    }
    
    public Long getFlightSchedulePlanID() {
        return flightSchedulePlanID;
    }

    public void setFlightSchedulePlanID(Long flightSchedulePlanID) {
        this.flightSchedulePlanID = flightSchedulePlanID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightSchedulePlanID != null ? flightSchedulePlanID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the flightSchedulePlanID fields are not set
        if (!(object instanceof FlightSchedulePlanEntity)) {
            return false;
        }
        FlightSchedulePlanEntity other = (FlightSchedulePlanEntity) object;
        if ((this.flightSchedulePlanID == null && other.flightSchedulePlanID != null) || (this.flightSchedulePlanID != null && !this.flightSchedulePlanID.equals(other.flightSchedulePlanID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightSchedulePlanEntity[ id=" + flightSchedulePlanID + " ]";
    }

    /**
     * @return the flight
     */
    public FlightEntity getFlight() {
        return flight;
    }

    /**
     * @param flight the flight to set
     */
    public void setFlight(FlightEntity flight) {
        this.flight = flight;
    }

    /**
     * @return the typeExistingInPlan
     */
    public ScheduleTypeEnum getTypeExistingInPlan() {
        return typeExistingInPlan;
    }

    /**
     * @param typeExistingInPlan the typeExistingInPlan to set
     */
    public void setTypeExistingInPlan(ScheduleTypeEnum typeExistingInPlan) {
        this.typeExistingInPlan = typeExistingInPlan;
    }

    /**
     * @return the fares
     */
    public List<FareEntity> getFares() {
        return fares;
    }

    /**
     * @param fares the fares to set
     */
    public void setFares(List<FareEntity> fares) {
        this.fares = fares;
    }

    /**
     * @return the recurrentEndDate
     */
    public Date getRecurrentEndDate() {
        return recurrentEndDate;
    }

    /**
     * @param recurrentEndDate the recurrentEndDate to set
     */
    public void setRecurrentEndDate(Date recurrentEndDate) {
        this.recurrentEndDate = recurrentEndDate;
    }

    /**
     * @return the disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * @param disabled the disabled to set
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public FlightSchedulePlanEntity getSource() {
        return source;
    }

    public void setSource(FlightSchedulePlanEntity source) {
        this.source = source;
    }

    public FlightSchedulePlanEntity getComplementary() {
        return complementary;
    }

    public void setComplementary(FlightSchedulePlanEntity complementary) {
        this.complementary = complementary;
    }
    
    
}
