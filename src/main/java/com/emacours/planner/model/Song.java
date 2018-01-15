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

    private final List<Player> players;
    private final String name;

    public String getName() {
        return name;
    }

    public Song(String name) {
        this.name = name;
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }
    
    @Override
    public String toString() {
        return "Song{" + "players=" + players + ", name=" + name + '}';
    }
    
}
