/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import javafx.beans.property.SimpleStringProperty;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author formation
 */
@Root(strict=false)

public class Player {

    private final SimpleStringProperty firstNameProperty;
    private final SimpleStringProperty lastNameProperty;

    @Attribute(required = true)
    public String getFirstName() {
        return firstNameProperty.get();
    }

    @Attribute(required = true)
    public void setFirstName(String firstName) {
        this.firstNameProperty.set(firstName);
    }

    @Attribute(required = true)
    public String getLastName() {
        return lastNameProperty.get();
    }

    @Attribute(required = true)
    public void setLastName(String lastName) {
        this.lastNameProperty.set(lastName);
    }

    public SimpleStringProperty getFirstNameProperty() {
        return firstNameProperty;
    }

    public SimpleStringProperty getLastNameProperty() {
        return lastNameProperty;
    }
    
    

    public Player(@Attribute (name = "firstName") String firstName, @Attribute (name = "lastName") String lastName) {
        this.firstNameProperty = new SimpleStringProperty(firstName);
        this.lastNameProperty = new SimpleStringProperty(lastName);
    }

    @Override
    public String toString() {
        return "Player{" + "firstName=" + firstNameProperty.get() + ", lastName=" + lastNameProperty.get() + '}';
    }

}
