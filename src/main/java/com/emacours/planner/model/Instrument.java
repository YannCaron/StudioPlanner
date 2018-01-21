/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author formation
 */
@Root(strict = false)
public class Instrument {

    private SimpleStringProperty nameProperty;

    @Attribute(required = true)
    public String getName() {
        return nameProperty.get();
    }

    @Attribute(required = true)
    public void setName(String name) {
        this.nameProperty.set(name);
    }

    public SimpleStringProperty getNameProperty() {
        return nameProperty;
    }

    public Instrument(@Attribute(name = "name") String name) {
        this.nameProperty = new SimpleStringProperty(name);
    }

    @Override
    public String toString() {
        return "Instrument{" + "name=" + nameProperty.get() + '}';
    }

}
