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
    private final String title;
    private final String author;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Song(String title, String author) {
        this.title = title;
        this.author = author;
        this.players = new ArrayList<>();
    }
    
    public void addPlayer(Player player) {
        players.add(player);
    }

    @Override
    public String toString() {
        return "Song{" + "title=" + title + ", author=" + author + "; players=" + players + '}';
    }

}
