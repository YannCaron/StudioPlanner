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

    private final Map<Song, Set<Song>> graph;
    private final Map<Song, Set<Player>> loosePlayers;

    public CompatibilityGraph(List<Song> songs) {
        this.graph = new HashMap<>();
        this.loosePlayers = new HashMap<>();
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
        }
        for (Song song1 : songs) {
            song1.calculateConstraint(graph.size() - (graph.get(song1).size() + getLooseSize(song1)), graph.size());
        }
    }

    private void addLoose(Song song, Player player) {
        Set<Player> set = loosePlayers.get(song);
        if (set == null) {
            set = new HashSet<>();
            loosePlayers.put(song, set);
        }
        set.add(player);
    }

    private int getLooseSize(Song song) {
        Set<Player> loose = loosePlayers.get(song);
        if (loose == null) {
            return 0;
        }
        return loose.size();
    }

    private boolean areCompatible(Song song1, Song song2) {
        if (song1 == song2) {
            return false;
        }

        for (Player player1 : song1.getPlayers()) {
            if (!player1.isLoose()) {
                for (Player player2 : song2.getPlayers()) {
                    if (player1 == player2) {
                        return false;
                    }
                }
            } else {
                addLoose(song1, player1);
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

    public Set<Player> queryLoose(Song song1) {
        return loosePlayers.get(song1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Compatobilities:\n");
        for (Entry<Song, Set<Song>> entry : graph.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" (");
            sb.append(entry.getKey().getConstraint());
            sb.append(")");
            sb.append(" -> ");
            sb.append(entry.getValue().toString());
            sb.append('\n');
        }
        sb.append("Loose Player:\n");
        for (Entry<Song, Set<Player>> entry : loosePlayers.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" -> ");
            sb.append(entry.getValue().toString());
            sb.append('\n');
        }
        return sb.toString();
    }

}
