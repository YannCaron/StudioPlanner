/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

/**
 *
 * @author formation
 */
public class Player {

    private final Person player;
    private final Instrument instrument;

    public Person getPlayer() {
        return player;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public Player(Person player, Instrument instrument) {
        this.player = player;
        this.instrument = instrument;
    }

    @Override
    public String toString() {
        return "Player{" + "player=" + player + ", instrument=" + instrument + '}';
    }

    
    
}
