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
    private final SimpleBooleanProperty LooseProperty;
    private boolean playSong;

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

    @Attribute(name = "loose", required = false)
    public boolean isLoose() {
        return LooseProperty.get();
    }

    @Attribute(name = "loose", required = false)
    public void setLoose(boolean loose) {
        this.LooseProperty.set(loose);
    }

    public boolean isPlaySong() {
        return playSong;
    }

    public SimpleStringProperty getFirstNameProperty() {
        return firstNameProperty;
    }

    public SimpleStringProperty getLastNameProperty() {
        return lastNameProperty;
    }

    public SimpleBooleanProperty getLooseProperty() {
        return LooseProperty;
    }

    public Player(@Attribute(name = "firstName") String firstName,
            @Attribute(name = "lastName") String lastName,
            @Attribute(name = "loose") Boolean loose) {
        this.firstNameProperty = new SimpleStringProperty(firstName);
        this.lastNameProperty = new SimpleStringProperty(lastName);

        if (loose == null) {
            loose = false;
        }
        this.LooseProperty = new SimpleBooleanProperty(loose);
        this.playSong = false;
    }

    public void applyPlaySong(Song song) {
        playSong = false;
        for (Player player : song.getPlayers()) {
            if (player == this) {
                playSong = true;
            }
        }
    }
    
    public void clearPlaySong() {
        playSong = false;
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
