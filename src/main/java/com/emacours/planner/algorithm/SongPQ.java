/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.algorithm;

import com.emacours.planner.model.Song;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author cyann
 */
public class SongPQ extends PriorityQueue<Song> {

    private static final Comparator<Song> MIN_CONSTRAINT_SONG = (Song o1, Song o2) -> {
        if (o1.getConstraint() < o2.getConstraint()) {
            return -1;
        } else if (o1.getConstraint() > o2.getConstraint()) {
            return 1;
        } else {
            return 0;
        }
    };

    public SongPQ(Collection<? extends Song> songs) {
        super(MIN_CONSTRAINT_SONG);
        super.addAll(songs);
    }

    public SongPQ clone() {
        return new SongPQ(this);
    }

}
