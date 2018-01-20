/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import org.simpleframework.xml.Element;

/**
 *
 * @author formation
 */
public class InstrumentPlayer {

    @Element
    private final Player player;

    @Element
    private final Instrument instrument;

    public Player getPlayer() {
        return player;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public InstrumentPlayer(Player player, Instrument instrument) {
        this.player = player;
        this.instrument = instrument;
    }

    @Override
    public String toString() {
        return "Player{" + "player=" + player + ", instrument=" + instrument + '}';
    }

}
