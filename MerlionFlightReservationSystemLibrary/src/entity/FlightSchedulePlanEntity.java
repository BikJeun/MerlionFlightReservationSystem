/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import enumeration.ScheduleTypeEnum;
import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

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
    @Column(nullable = false, unique = true, length = 32)
    private String flightNum;
    
    private ArrayList<ScheduleTypeEnum> typeExistingInPlan;
    
    @ManyToMany
    private ArrayList<FlightScheduleEntity> flightSchedule;

    public FlightSchedulePlanEntity() {
        typeExistingInPlan = new ArrayList<>(4);
        flightSchedule = new ArrayList<>();
    }

    public FlightSchedulePlanEntity(String flightNum, ArrayList<FlightScheduleEntity> flightSchedule) {
        this.flightNum = flightNum;
        this.flightSchedule = flightSchedule;
    }

    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    public ArrayList<FlightScheduleEntity> getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(ArrayList<FlightScheduleEntity> flightSchedule) {
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
    
}
