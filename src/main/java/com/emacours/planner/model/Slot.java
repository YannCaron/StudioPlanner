/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cyann
 */
public class Slot {

    private final Map<Studio, Song> songs;
    private final String name;

    public String getName() {
        return name;
    }

    public Slot(String name) {
        this.name = name;
        songs = new HashMap<>();
    }

    public void addSong(Studio studio, Song song) {
        songs.put(studio, song);
    }

    public Song getSong(Studio studio) {
        return songs.get(studio);
    }
    
    public boolean isEmpty() {
        return songs.isEmpty();
    }

    @Override
    public String toString() {
        return "Slot{" + "songs=" + songs + '}';
    }

}
