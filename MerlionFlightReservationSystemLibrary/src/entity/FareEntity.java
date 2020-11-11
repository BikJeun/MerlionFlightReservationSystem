/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package entity;

import enumeration.CabinClassTypeEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
@Table(uniqueConstraints=
       @UniqueConstraint(columnNames = {"fareBasisCode", "flightSchedulePlan_flightSchedulePlanId", "cabinClassType"})) 
public class FareEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fareID;
    
    /* Fare codes start with a
    letter that denotes the booking class. Other letters or numbers may follow. Typically a fare
    basis will be 3 to 7 characters long.*/
    @Column(nullable = false, length = 7)
    @NotNull
    private String fareBasisCode;
    
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    private BigDecimal fareAmount;

    @Column(nullable = false)
    @NotNull
    private CabinClassTypeEnum cabinClassType;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FlightSchedulePlanEntity flightSchedulePlan;

    public FareEntity() {
    }

    public FareEntity(String fareBasisCode, BigDecimal fareAmount, CabinClassTypeEnum cabinClassType) {
        this();
        this.fareBasisCode = fareBasisCode;
        this.fareAmount = fareAmount;
        this.cabinClassType = cabinClassType;
    }


    public String getFareBasisCode() {
        return fareBasisCode;
    }

    public void setFareBasisCode(String fareBasisCode) {
        this.fareBasisCode = fareBasisCode;
    }

    public BigDecimal getFareAmount() {
        return fareAmount;
    }

    public void setFareAmount(BigDecimal fareAmount) {
        this.fareAmount = fareAmount;
    }
    
    public Long getFareID() {
        return fareID;
    }
    
    public void setFareID(Long fareID) {
        this.fareID = fareID;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fareID != null ? fareID.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the fareID fields are not set
        if (!(object instanceof FareEntity)) {
            return false;
        }
        FareEntity other = (FareEntity) object;
        if ((this.fareID == null && other.fareID != null) || (this.fareID != null && !this.fareID.equals(other.fareID))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "entity.FareEntity[ id=" + fareID + " ]";
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

    /**
     * @return the cabinClassType
     */
    public CabinClassTypeEnum getCabinClassType() {
        return cabinClassType;
    }

    /**
     * @param cabinClassType the cabinClassType to set
     */
    public void setCabinClassType(CabinClassTypeEnum cabinClassType) {
        this.cabinClassType = cabinClassType;
    }
    
}
