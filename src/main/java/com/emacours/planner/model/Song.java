/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author formation
 */
public class Song {

    public static final int STUDIO_CONSTRAINT_VALUE = 5;
    
    private final String title;
    private final String author;

    // constraints
    private final List<Person> players;
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
        this.players = new ArrayList<>();
    }

    public void addPlayer(Person player) {
        players.add(player);
    }

    public int countConstraint() {
        return players.size() + (hasPreferedStudio() ? STUDIO_CONSTRAINT_VALUE : 0);
    }

    @Override
    public String toString() {
        return "Song{" + "title=" + title + ", author=" + author + "; players=" + players + '}';
    }

}
