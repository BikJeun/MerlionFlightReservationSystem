/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class FlightRouteEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightRouteID;
    
    @OneToOne
    private ODPairEntity odPair;
    @OneToMany(mappedBy = "flightRoute")
    private ArrayList<FlightEntity> flights;
    /*@ManyToMany(mappedBy = "airport")
    private ArrayList<AirportEntity> airports;*/

    public FlightRouteEntity() {
        flights = new ArrayList<>();  
    }

    public FlightRouteEntity(ODPairEntity odPair) {
        this();
        this.odPair = odPair;
    }

    public ODPairEntity getOdPair() {
        return odPair;
    }

    public void setOdPair(ODPairEntity odPair) {
        this.odPair = odPair;
    }
    

    public Long getFlightRouteID() {
        return flightRouteID;
    }

    public void setFlightRouteID(Long flightRouteID) {
        this.flightRouteID = flightRouteID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightRouteID != null ? flightRouteID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the flightRouteID fields are not set
        if (!(object instanceof FlightRouteEntity)) {
            return false;
        }
        FlightRouteEntity other = (FlightRouteEntity) object;
        if ((this.flightRouteID == null && other.flightRouteID != null) || (this.flightRouteID != null && !this.flightRouteID.equals(other.flightRouteID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightRouteEntity[ id=" + flightRouteID + " ]";
    }
    
}
