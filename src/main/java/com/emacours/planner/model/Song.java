/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import java.util.Collection;
import java.util.HashMap;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

/**
 *
 * @author formation
 */
@Root(strict = false)
public class Song {

    private final SimpleStringProperty nameProperty;
    private final SimpleStringProperty commentProperty;
    private int constraint = 0;

    // constraints
    @ElementMap(name = "instrumentPlayers", inline = true, required = false)
    private final HashMap<Instrument, Player> instrumentPlayers;

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

    @Attribute(required = false)
    public String getComment() {
        return commentProperty.get();
    }

    @Attribute(required = false)
    public void setComment(String name) {
        this.commentProperty.set(name);
    }

    public SimpleStringProperty getCommentProperty() {
        return commentProperty;
    }

    public void calculateConstraint(int score, int studioScore) {
        constraint = score + (hasPreferedStudio() ? studioScore : 0);
    }
    
    public int getConstraint() {
        return constraint;
    }

    public Song(@Attribute(name = "name") String name,
            @Attribute(name = "comment", required = false) String comment,
            @Element(name = "preferedStudio", required = false) Studio preferedStudio,
            @ElementMap(name = "instrumentPlayers", inline = true, required = false) HashMap<Instrument, Player> instrumentPlayers) {
        this.nameProperty = new SimpleStringProperty(name);
        this.commentProperty = new SimpleStringProperty(comment);
        this.preferedStudioProperty = new SimpleObjectProperty<>(preferedStudio);
        if (instrumentPlayers == null) {
            instrumentPlayers = new HashMap<>();
        }
        this.instrumentPlayers = instrumentPlayers;
    }

    public Song(String name) {
        this.nameProperty = new SimpleStringProperty(name);
        this.commentProperty = new SimpleStringProperty();
        this.preferedStudioProperty = new SimpleObjectProperty<>();
        instrumentPlayers = new HashMap<>();
    }

    @Element(name = "preferedStudio", required = false)
    public Studio getPreferedStudio() {
        if (preferedStudioProperty == null) {
            return null;
        }
        return preferedStudioProperty.get();
    }

    @Element(name = "preferedStudio", required = false)
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
        return preferedStudioProperty != null && preferedStudioProperty.get() != null;
    }

    public void addPlayer(Instrument instrument, Player player) {
        instrumentPlayers.put(instrument, player);
    }
    
    public void removePlayer(Player player) {
        Instrument foundInstrument = null;
        for (Instrument instrument : instrumentPlayers.keySet()) {
            if (instrumentPlayers.get(instrument) == player){
                foundInstrument = instrument;
            }
        }
        
        instrumentPlayers.remove(foundInstrument);
    }

    public Collection<Player> getPlayers() {
        return instrumentPlayers.values();
    }

    public SimpleObjectProperty<Player> getPlayerProperty(final Instrument instrument) {
        SimpleObjectProperty<Player> property = new SimpleObjectProperty<>(instrumentPlayers.get(instrument));
        /*property.addListener((observable, oldValue, newValue) -> {
            addPlayer(instrument, newValue);
        });*/
        return property;
    }

    @Override
    public String toString() {
        return nameProperty.get();
    }

}
