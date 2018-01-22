/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.algorithm;

import com.emacours.planner.model.Player;
import com.emacours.planner.model.Song;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author cyann
 */
public class CompatibilityGraph {

    private Map<Song, Set<Song>> graph;

    public CompatibilityGraph(List<Song> songs) {
        this.graph = new HashMap<>();
        computeGraph(songs);
    }

    private void computeGraph(List<Song> songs) {
        for (Song song1 : songs) {
            Set<Song> set = new HashSet<>();
            for (Song song2 : songs) {
                if (areCompatible(song1, song2)) {
                    set.add(song2);
                }
                graph.put(song1, set);
            }
            song1.calculateConstraint(set.size(), songs.size());
        }
    }

    private boolean areCompatible(Song song1, Song song2) {
        if (song1 == song2) {
            return false;
        }
        for (Player player1 : song1.getPlayers()) {
            //if (player1.isFree()) return true;
            for (Player player2 : song2.getPlayers()) {
                if (player1 == player2 && !player2.isFree()) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<Song, Set<Song>> entry : graph.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" (");
            sb.append(entry.getValue().size());
            sb.append(")");
            sb.append(" -> ");
            sb.append(entry.getValue().toString());
            sb.append('\n');
        }
        return sb.toString();
    }

}
