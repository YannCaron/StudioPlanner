/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.algorithm;

import com.emacours.planner.model.InstrumentPlayer;
import com.emacours.planner.model.Song;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author cyann
 */
public class CompatibilityGraph {

    private final List<Song> songs;
    private Map<Song, Set<Song>> graph;

    public CompatibilityGraph(List<Song> songs) {
        this.songs = songs;
        this.graph = new HashMap<>();
        computeGraph();
    }

    private void computeGraph() {
        for (Song song1 : songs) {
            Set<Song> set = new HashSet<>();
            for (Song song2 : songs) {
                if (areCompatible(song1, song2)) {
                    set.add(song2);
                }
                graph.put(song1, set);
            }
        }
    }

    private boolean areCompatible(Song song1, Song song2) {
        if (song1 == song2) {
            return false;
        }
        for (InstrumentPlayer instrumentPlayer1 : song1.getPlayers()) {
            for (InstrumentPlayer instrumentPlayer2 : song2.getPlayers()) {
                if (instrumentPlayer1.getPlayer() == instrumentPlayer2.getPlayer()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean query(Song song1, Song song2) {
        if (!graph.containsKey(song1)) {
            return false;
        }
        
        return graph.get(song1).contains(song2);
    }

}
