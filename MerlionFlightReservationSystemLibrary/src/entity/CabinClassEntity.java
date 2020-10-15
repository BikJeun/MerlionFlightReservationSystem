/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import enumeration.CabinClassTypeEnum;
import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 *
 * @author Mitsuki
 */
@Entity
public class CabinClassEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cabinClassID;
    @Enumerated(EnumType.STRING)
    private CabinClassTypeEnum cabinClassType;
    @Column(nullable = false)
    @Min(0)
    @Max(2)
    private int numOfAisles;
    @Column(nullable = false)
    private int numOfRows;
    @Column(nullable = false)
    private int numOfSeatsAbreast;
    @Column(nullable = false, length = 5)
    private String seatingConfigPerColumn;
    @Column(nullable = false)
    private int maxSeatCapacity;
    
    @OneToMany(mappedBy = "cabinClass", fetch = FetchType.EAGER)
    private ArrayList<FareEntity> fare;
    @ManyToMany(mappedBy = "cabinClass", fetch = FetchType.EAGER)
    private ArrayList<AircraftConfigurationEntity> aircraftConfig;
    @OneToOne
    private SeatInventoryEntity seatInventory;

    public CabinClassEntity() {
        fare = new ArrayList<>();
        aircraftConfig = new ArrayList<>();
    }

    public CabinClassEntity(CabinClassTypeEnum cabinClassType, int numOfAisles, int numOfRows, int numOfSeatsAbreast, String seatingConfigPerColumn, int maxSeatCapacity, ArrayList<FareEntity> fare, ArrayList<AircraftConfigurationEntity> aircraftConfig, SeatInventoryEntity seatInventory) {
        this();
        this.cabinClassType = cabinClassType;
        this.numOfAisles = numOfAisles;
        this.numOfRows = numOfRows;
        this.numOfSeatsAbreast = numOfSeatsAbreast;
        this.seatingConfigPerColumn = seatingConfigPerColumn;
        this.maxSeatCapacity = maxSeatCapacity;
        this.fare = fare;
        this.aircraftConfig = aircraftConfig;
        this.seatInventory = seatInventory;
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

    public ArrayList<FareEntity> getFare() {
        return fare;
    }

    public void setFare(ArrayList<FareEntity> fare) {
        this.fare = fare;
    }

    public ArrayList<AircraftConfigurationEntity> getAircraftConfig() {
        return aircraftConfig;
    }

    public void setAircraftConfig(ArrayList<AircraftConfigurationEntity> aircraftConfig) {
        this.aircraftConfig = aircraftConfig;
    }

    public SeatInventoryEntity getSeatInventory() {
        return seatInventory;
    }

    public void setSeatInventory(SeatInventoryEntity seatInventory) {
        this.seatInventory = seatInventory;
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
        if (!(object instanceof CabinClassEntity)) {
            return false;
        }
        CabinClassEntity other = (CabinClassEntity) object;
        if ((this.cabinClassID == null && other.cabinClassID != null) || (this.cabinClassID != null && !this.cabinClassID.equals(other.cabinClassID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CabinClassEntity[ id=" + cabinClassID + " ]";
    }
    
}
