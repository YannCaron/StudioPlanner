/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import com.emacours.planner.algorithm.CompatibilityGraph;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cyann
 */
public class Slot {

    public static class ItemInfo {

        Song song;
        boolean canAccept;

        public ItemInfo(Song song) {
            this.song = song;
            this.canAccept = true;
        }

    }

    private final Map<Studio, ItemInfo> songs;
    private final String name;

    public String getName() {
        return name;
    }

    public Slot(String name) {
        this.name = name;
        songs = new HashMap<>();
    }

    public void addSong(Studio studio, Song song) {
        songs.put(studio, new ItemInfo(song));
    }

    public Song getSong(Studio studio) {
        ItemInfo itemInfo = songs.get(studio);
        if (itemInfo == null) {
            return null;
        }
        return itemInfo.song;
    }

    public boolean getCanAccept(Studio studio) {
        ItemInfo itemInfo = songs.get(studio);
        if (itemInfo == null) {
            return true;
        }

        return itemInfo.canAccept;
    }

    public void applyAccept(CompatibilityGraph graph, Song song, Song dummy) {
        
        for (ItemInfo info1 : songs.values()) {
            boolean canAccept = true;

            for (ItemInfo info2 : songs.values()) {
                if (info1 != info2 && song != dummy && info2.song != dummy && 
                        song != info2.song &&
                        !graph.query(song, info2.song)) {
                    canAccept = false;
                }
            }

            info1.canAccept = canAccept;
        }
    }

    public boolean isEmpty() {
        return songs.isEmpty();
    }

    @Override
    public String toString() {
        return "Slot{" + "songs=" + songs + '}';
    }

}
