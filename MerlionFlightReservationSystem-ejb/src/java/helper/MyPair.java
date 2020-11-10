/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import entity.FlightScheduleEntity;

/**
 *
 * @author Mitsuki
 */
public class MyPair {
    
    FlightScheduleEntity fs1;
    FlightScheduleEntity fs2;

    public MyPair(FlightScheduleEntity fs1, FlightScheduleEntity fs2) {
        this.fs1 = fs1;
        this.fs2 = fs2;
    }

    public FlightScheduleEntity getKey() {
        return fs1;
    }

    public void setFs1(FlightScheduleEntity fs1) {
        this.fs1 = fs1;
    }

    public FlightScheduleEntity getValue() {
        return fs2;
    }

    public void setFs2(FlightScheduleEntity fs2) {
        this.fs2 = fs2;
    }
    
    
    
    
    
}
