/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author formation
 */
@Root(strict = false)

public class Player {

    private final SimpleStringProperty firstNameProperty;
    private final SimpleStringProperty lastNameProperty;
    private final SimpleBooleanProperty freeProperty;

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

    public boolean isFree() {
        return freeProperty.get();
    }

    public void setFree(boolean free) {
        this.freeProperty.set(free);
    }

    public SimpleStringProperty getFirstNameProperty() {
        return firstNameProperty;
    }

    public SimpleStringProperty getLastNameProperty() {
        return lastNameProperty;
    }

    public SimpleBooleanProperty getFreeProperty() {
        return freeProperty;
    }

    public Player(@Attribute(name = "firstName") String firstName, @Attribute(name = "lastName") String lastName) {
        this.firstNameProperty = new SimpleStringProperty(firstName);
        this.lastNameProperty = new SimpleStringProperty(lastName);
        this.freeProperty = new SimpleBooleanProperty(false);
    }

    @Override
    public String toString() {
        if (!"".equals(lastNameProperty.get())) {
            return firstNameProperty.get() + " " + lastNameProperty.get();
        } else {
            return firstNameProperty.get();
        }
    }

}
