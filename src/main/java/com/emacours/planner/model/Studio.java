/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import javafx.beans.property.SimpleStringProperty;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Root;

/**
 *
 * @author formation
 */
@Root
@Default(DefaultType.PROPERTY)
public class Studio {

    private final SimpleStringProperty name;

    @Attribute(required = true)
    public String getName() {
        return name.get();
    }

    @Attribute(required = true)
    public void setName(String name) {
        this.name.set(name);
    }

    public Studio(String Name) {
        this.name = new SimpleStringProperty(Name);
    }

    @Override
    public String toString() {
        return "Studio{" + "Name=" + name.get() + '}';
    }

}
