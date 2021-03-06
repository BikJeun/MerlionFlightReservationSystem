/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class AircraftConfigurationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aircraftConfigID;
    
    @Column(nullable = false, unique = true, length = 64)
    @Size(min = 1, max = 64)
    @NotNull
    private String name;
    
    @Column(nullable = false)
    @Min(1)
    @Max(4)
    @NotNull
    private int numberOfCabinClasses;
    
    @ManyToOne(optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(nullable = false)
    private AircraftTypeEntity aircraftType;
    
    // How to enforce 1..*? (Right now only enforced in session bean)
    @OneToMany(mappedBy = "aircraftConfig", fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private List<CabinClassEntity> cabin;

    public AircraftConfigurationEntity() {
        cabin = new ArrayList<>();
    }

    public AircraftConfigurationEntity(String name, int numberOfCabinClasses) {
        this();
        this.name = name;
        this.numberOfCabinClasses = numberOfCabinClasses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfCabinClasses() {
        return numberOfCabinClasses;
    }

    public void setNumberOfCabinClasses(int numberOfCabinClasses) {
        this.numberOfCabinClasses = numberOfCabinClasses;
    }

    public AircraftTypeEntity getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(AircraftTypeEntity aircraftType) {
        this.aircraftType = aircraftType;
    }

    public List<CabinClassEntity> getCabin() {
        return cabin;
    }

    public void setCabin(List<CabinClassEntity> cabin) {
        this.cabin = cabin;
    }  

    public Long getAircraftConfigID() {
        return aircraftConfigID;
    }

    public void setAircraftConfigID(Long aircraftConfigID) {
        this.aircraftConfigID = aircraftConfigID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aircraftConfigID != null ? aircraftConfigID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the aircraftConfigID fields are not set
        if (!(object instanceof AircraftConfigurationEntity)) {
            return false;
        }
        AircraftConfigurationEntity other = (AircraftConfigurationEntity) object;
        if ((this.aircraftConfigID == null && other.aircraftConfigID != null) || (this.aircraftConfigID != null && !this.aircraftConfigID.equals(other.aircraftConfigID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AircraftConfigurationEntity[ id=" + aircraftConfigID + " ]";
    }
    
}
