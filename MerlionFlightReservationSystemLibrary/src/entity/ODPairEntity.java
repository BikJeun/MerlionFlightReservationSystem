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
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
@Table(uniqueConstraints=
       @UniqueConstraint(columnNames = {"origin", "destination"}))
public class ODPairEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ODPairID;
    @JoinColumn(nullable = false)
    private AirportEntity origin;
    @JoinColumn(nullable = false)
    private AirportEntity destination;

    public ODPairEntity() {
    }

    public ODPairEntity(AirportEntity origin, AirportEntity destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public AirportEntity getOrigin() {
        return origin;
    }

    public void setOrigin(AirportEntity origin) {
        this.origin = origin;
    }

    public AirportEntity getDestination() {
        return destination;
    }

    public void setDestination(AirportEntity destination) {
        this.destination = destination;
    }

    public Long getODPairID() {
        return ODPairID;
    }

    public void setODPairID(Long ODPairID) {
        this.ODPairID = ODPairID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ODPairID != null ? ODPairID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the ODPairID fields are not set
        if (!(object instanceof ODPairEntity)) {
            return false;
        }
        ODPairEntity other = (ODPairEntity) object;
        if ((this.ODPairID == null && other.ODPairID != null) || (this.ODPairID != null && !this.ODPairID.equals(other.ODPairID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ODPairEntity[ id=" + ODPairID + " ]";
    }
    
}
