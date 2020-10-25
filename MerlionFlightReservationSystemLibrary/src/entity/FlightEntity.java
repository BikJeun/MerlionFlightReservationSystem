/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.OneToOne;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class FlightEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long FlightID;
    @Column(nullable = false, unique = true, length = 32)
    private String flightNum;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FlightRouteEntity flightRoute;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private AircraftConfigurationEntity aircraftConfig;
    
    @OneToMany(mappedBy = "flight", fetch = FetchType.EAGER)
    private List<FlightSchedulePlanEntity> flightSchedulePlan;
    
    //One-to-One self referencing constraints (please check)
    //QN: Why is a self referencing like this??
    @OneToOne
    private FlightEntity returningFlight;
    @OneToOne(mappedBy = "returningFlight")
    private FlightEntity sourceFlight;

    public FlightEntity() {
        flightSchedulePlan = new ArrayList<>();
        returningFlight = null;
    }

    public FlightEntity(String flightNum, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig, List<FlightSchedulePlanEntity> flightSchedulePlan) {
        this();
        this.flightNum = flightNum;
        this.flightRoute = flightRoute;
        this.aircraftConfig = aircraftConfig;
        this.flightSchedulePlan = flightSchedulePlan;
    }

    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    public FlightRouteEntity getFlightRoute() {
        return flightRoute;
    }

    public void setFlightRoute(FlightRouteEntity flightRoute) {
        this.flightRoute = flightRoute;
    }

    public AircraftConfigurationEntity getAircraftConfig() {
        return aircraftConfig;
    }

    public void setAircraftConfig(AircraftConfigurationEntity aircraftConfig) {
        this.aircraftConfig = aircraftConfig;
    }

    public List<FlightSchedulePlanEntity> getFlightSchedulePlan() {
        return flightSchedulePlan;
    }

    public void setFlightSchedulePlan(List<FlightSchedulePlanEntity> flightSchedule) {
        this.flightSchedulePlan = flightSchedule;
    }
    
    

    public Long getFlightID() {
        return FlightID;
    }

    public void setFlightID(Long FlightID) {
        this.FlightID = FlightID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (FlightID != null ? FlightID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the FlightID fields are not set
        if (!(object instanceof FlightEntity)) {
            return false;
        }
        FlightEntity other = (FlightEntity) object;
        if ((this.FlightID == null && other.FlightID != null) || (this.FlightID != null && !this.FlightID.equals(other.FlightID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightEntity[ id=" + FlightID + " ]";
    }

    /**
     * @return the returningFlight
     */
    public FlightEntity getReturningFlight() {
        return returningFlight;
    }

    /**
     * @param returningFlight the returningFlight to set
     */
    public void setReturningFlight(FlightEntity returningFlight) {
        this.returningFlight = returningFlight;
    }

    /**
     * @return the sourceFlight
     */
    public FlightEntity getSourceFlight() {
        return sourceFlight;
    }

    /**
     * @param sourceFlight the sourceFlight to set
     */
    public void setSourceFlight(FlightEntity sourceFlight) {
        this.sourceFlight = sourceFlight;
    }
    
}
