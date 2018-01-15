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
public class Studio {

    private final String Name;

    public String getName() {
        return Name;
    }

    public Studio(String Name) {
        this.Name = Name;
    }

    @Override
    public String toString() {
        return "Studio{" + "Name=" + Name + '}';
    }

}
