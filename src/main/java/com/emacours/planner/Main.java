/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

import com.emacours.planner.model.Instrument;
import com.emacours.planner.model.Studio;

/**
 *
 * @author formation
 */
public class Main {
    
    private static final Instrument BASS = new Instrument("bass");
    private static final Instrument GUITARE = new Instrument("guitare");
    private static final Instrument VOICE = new Instrument("voice");
    private static final Instrument DRUM = new Instrument("drum");
    private static final Instrument PERCUSION = new Instrument("percusion");
    
    private static final Studio STUDIO_A = new Studio("Studio A");
    private static final Studio STUDIO_B = new Studio("Studio B");
    private static final Studio STUDIO_C = new Studio("Studio C");
    
    
    
    public static void main(String[] args) {
        System.out.println(BASS);
    }
    
}
