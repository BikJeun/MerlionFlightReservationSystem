/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import enumeration.CabinClassTypeEnum;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class CabinClassEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cabinClassID;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CabinClassTypeEnum cabinClassType;
    
    @Column(nullable = false)
    @Min(0)
    @Max(2)
    private int numOfAisles;
    
    @Column(nullable = false)
    @Min(1)
    private int numOfRows;
    
    @Column(nullable = false)
    private int numOfSeatsAbreast;
    
    @Column(nullable = false)
    @Size(min=1, max=5)
    private String seatingConfigPerColumn;
    
    @Column(nullable = false)
    @Min(1)
    private int maxSeatCapacity;
    
    @ManyToOne(optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(nullable = false)
    private AircraftConfigurationEntity aircraftConfig;

    public CabinClassEntity() { 
    }

    public CabinClassEntity(CabinClassTypeEnum cabinClassType, int numOfAisles, int numOfRows, int numOfSeatsAbreast, String seatingConfigPerColumn, int maxSeatCapacity) {
        this.cabinClassType = cabinClassType;
        this.numOfAisles = numOfAisles;
        this.numOfRows = numOfRows;
        this.numOfSeatsAbreast = numOfSeatsAbreast;
        this.seatingConfigPerColumn = seatingConfigPerColumn;
        this.maxSeatCapacity = maxSeatCapacity;
    }

    public CabinClassTypeEnum getCabinClassType() {
        return cabinClassType;
    }

    public void setCabinClassType(CabinClassTypeEnum cabinClassType) {
        this.cabinClassType = cabinClassType;
    }

    public int getNumOfAisles() {
        return numOfAisles;
    }

    public void setNumOfAisles(int numOfAisles) {
        this.numOfAisles = numOfAisles;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(int numOfRows) {
        this.numOfRows = numOfRows;
    }

    public int getNumOfSeatsAbreast() {
        return numOfSeatsAbreast;
    }

    public void setNumOfSeatsAbreast(int numOfSeatsAbreast) {
        this.numOfSeatsAbreast = numOfSeatsAbreast;
    }

    public String getSeatingConfigPerColumn() {
        return seatingConfigPerColumn;
    }

    public void setSeatingConfigPerColumn(String seatingConfigPerColumn) {
        this.seatingConfigPerColumn = seatingConfigPerColumn;
    }

    public int getMaxSeatCapacity() {
        return maxSeatCapacity;
    }

    public void setMaxSeatCapacity(int maxSeatCapacity) {
        this.maxSeatCapacity = maxSeatCapacity;
    }   

    public Long getCabinClassID() {
        return cabinClassID;
    }

    public void setCabinClassID(Long cabinClassID) {
        this.cabinClassID = cabinClassID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cabinClassID != null ? cabinClassID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the cabinClassID fields are not set
        // EDIT (BY JUNHAO): Modified to account for when cabinClassID not set (used in sessionbean)
        if (!(object instanceof CabinClassEntity)) {
            return false;
        }
        CabinClassEntity other = (CabinClassEntity) object;
        if ((this.cabinClassID == null && other.cabinClassID != null) || (this.cabinClassID != null && !this.cabinClassID.equals(other.cabinClassID)) || (this.cabinClassID == null && other.cabinClassID == null) ) {
            return false;
        } 
        return true;
    }

    @Override
    public String toString() {
        return "entity.CabinClassEntity[ id=" + cabinClassID + " ]";
    }

    /**
     * @return the aircraftConfig
     */
    public AircraftConfigurationEntity getAircraftConfig() {
        return aircraftConfig;
    }

    /**
     * @param aircraftConfig the aircraftConfig to set
     */
    public void setAircraftConfig(AircraftConfigurationEntity aircraftConfig) {
        this.aircraftConfig = aircraftConfig;
    }
    
}
