/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 *
 * @author formation
 */
public class Song {

    public static final int STUDIO_CONSTRAINT_VALUE = 10;
    
    @Attribute
    private final String title;
    @Attribute
    private final String author;

    // constraints
    @ElementList(inline = true)
    private final List<InstrumentPlayer> instrumentPlayers;
    @Element(required = false)
    private Studio preferedStudio = null;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Studio getPreferedStudio() {
        return preferedStudio;
    }

    public void setPreferedStudio(Studio preferedStudio) {
        this.preferedStudio = preferedStudio;
    }
    
    public boolean hasPreferedStudio() {
        return preferedStudio != null;
    }

    public Song(String title, String author) {
        this.title = title;
        this.author = author;
        this.instrumentPlayers = new ArrayList<>();
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
        return author;
    }

}
