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
public class Instrument {

    private final String name;

    public String getName() {
        return name;
    }

    public Instrument(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Instrument{" + "name=" + name + '}';
    }

}
