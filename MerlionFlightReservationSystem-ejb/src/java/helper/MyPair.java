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
    
    private FlightScheduleEntity fs1;
    private FlightScheduleEntity fs2;

    public MyPair() {
    }

    public MyPair(FlightScheduleEntity fs1, FlightScheduleEntity fs2) {
        this.fs1 = fs1;
        this.fs2 = fs2;
    }

    public FlightScheduleEntity getFs1() {
        return fs1;
    }

    public void setFs1(FlightScheduleEntity fs1) {
        this.fs1 = fs1;
    }

    public FlightScheduleEntity getFs2() {
        return fs2;
    }

    public void setFs2(FlightScheduleEntity fs2) {
        this.fs2 = fs2;
    }
    
    
    
    
    
}
