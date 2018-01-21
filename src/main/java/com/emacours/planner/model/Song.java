/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author formation
 */
@Root(strict = false)
public class Song {

    public static final int STUDIO_CONSTRAINT_VALUE = 10;

    private SimpleStringProperty nameProperty;

    // constraints
    //@ElementList(inline = true)
    private final List<InstrumentPlayer> instrumentPlayers;
    //@Element(required = false)
    private SimpleObjectProperty<Studio> preferedStudioProperty;

    @Attribute
    public String getName() {
        return nameProperty.get();
    }

    @Attribute
    public void setName(String name) {
        this.nameProperty.set(name);
    }

    public SimpleStringProperty getNameProperty() {
        return nameProperty;
    }

    public Song(@Attribute(name = "name") String name,
            @Attribute(name = "preferedStudio", required = false) Studio preferedStudio) {

        this.nameProperty = new SimpleStringProperty(name);
        this.preferedStudioProperty = new SimpleObjectProperty<>(preferedStudio);
        instrumentPlayers = new ArrayList<>();
    }

    public Song(String name) {
        this.nameProperty = new SimpleStringProperty(name);
        instrumentPlayers = new ArrayList<>();
    }

    @Attribute(required = false)
    public Studio getPreferedStudio() {
        if (preferedStudioProperty == null) {
            return null;
        }
        return preferedStudioProperty.get();
    }

    @Attribute(required = false)
    public void setPreferedStudio(Studio preferedStudio) {
        if (preferedStudioProperty == null) {
            preferedStudioProperty = new SimpleObjectProperty<>(preferedStudio);
        } else {
            this.preferedStudioProperty.set(preferedStudio);
        }
    }

    public SimpleObjectProperty<Studio> getPreferedStudioProperty() {
        return preferedStudioProperty;
    }

    public boolean hasPreferedStudio() {
        return preferedStudioProperty != null;
    }

    public void addPlayer(InstrumentPlayer player) {
        instrumentPlayers.add(player);
    }

    public List<InstrumentPlayer> getPlayers() {
        return instrumentPlayers;
    }

    public int countConstraint() {
        return instrumentPlayers.size() + (hasPreferedStudio() ? STUDIO_CONSTRAINT_VALUE : 0);
    }

    @Override
    public String toString() {
        return "Song{" + "nameProperty=" + nameProperty.get() + ", preferedStudioProperty=" + preferedStudioProperty + '}';
    }

    
}
