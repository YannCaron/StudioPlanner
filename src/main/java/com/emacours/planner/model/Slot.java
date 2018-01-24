/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import com.emacours.planner.algorithm.CompatibilityGraph;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cyann
 */
public class Slot {

    public enum Action {
        NONE, REPLACE, SWITCH
    }

    public static class ItemInfo {

        Song song;
        Action possibleAction;

        public ItemInfo(Song song) {
            this.song = song;
            this.possibleAction = Action.SWITCH;
        }

    }

    private final Map<Studio, ItemInfo> infos;
    private final String name;

    public String getName() {
        return name;
    }

    public Slot(String name) {
        this.name = name;
        infos = new HashMap<>();
    }

    public void addSong(Studio studio, Song song) {
        infos.put(studio, new ItemInfo(song));
    }

    public Song getSong(Studio studio) {
        ItemInfo itemInfo = infos.get(studio);
        if (itemInfo == null) {
            return null;
        }
        return itemInfo.song;
    }

    public Action getPossibleAction(Studio studio) {
        ItemInfo itemInfo = infos.get(studio);
        if (itemInfo == null) {
            return Action.SWITCH;
        }

        return itemInfo.possibleAction;
    }

    public void setPossibleAction(Studio studio, Action action) {
        ItemInfo itemInfo = infos.get(studio);
        itemInfo.possibleAction = action;
    }

    public void applyPossibleAction(CompatibilityGraph graph, Song dummy, Slot other, Studio studio) {

        Song song = other.getSong(studio);

        for (ItemInfo info : infos.values()) {
            Action possibleAction = Action.SWITCH;

            if (!accept(graph, dummy, song, info.song)) {
                possibleAction = Action.NONE;
            } else if (!other.accept(graph, dummy, info.song, song)) {
                possibleAction = Action.REPLACE;
            }

            info.possibleAction = possibleAction;
        }

    }

    public void clearPossibleAction() {
        infos.values().forEach((info) -> info.possibleAction = Action.SWITCH);
    }
    
    public void applyPlayer(Player player) {
        for (ItemInfo info : infos.values()) {
            info.possibleAction = Action.SWITCH;
            for (Player other : info.song.getPlayers()) {
                if (player == other) {
                    info.possibleAction = Action.NONE;
                }
            }
        }
    }

    private boolean accept(CompatibilityGraph graph, Song dummy, Song song, Song current) {
        boolean value = true;
        for (ItemInfo info : infos.values()) {
            if (current != info.song && song != dummy && info.song != dummy
                    && song != info.song
                    && !graph.query(song, info.song)) {
                value = false;
            }
        }
        return value;
    }

    public boolean isEmpty() {
        return infos.isEmpty();
    }

    @Override
    public String toString() {
        return "Slot{" + "songs=" + infos + '}';
    }

}
