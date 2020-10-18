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
    @OneToOne(optional = false)
    private AircraftConfigurationEntity aircraftConfig;
    @OneToMany(mappedBy = "flight")
    private List<FlightScheduleEntity> flightSchedule;

    public FlightEntity() {
        flightSchedule = new ArrayList<>();
    }

    public FlightEntity(String flightNum, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig, List<FlightScheduleEntity> flightSchedule) {
        this();
        this.flightNum = flightNum;
        this.flightRoute = flightRoute;
        this.aircraftConfig = aircraftConfig;
        this.flightSchedule = flightSchedule;
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

    public List<FlightScheduleEntity> getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(List<FlightScheduleEntity> flightSchedule) {
        this.flightSchedule = flightSchedule;
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
    
}
