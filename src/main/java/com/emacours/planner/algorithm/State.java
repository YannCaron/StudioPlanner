/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.algorithm;

import com.emacours.planner.model.Song;
import java.util.Arrays;
import java.util.PriorityQueue;

/**
 *
 * @author cyann
 */
public class State {

    public final int position;
    public final Song[] planning;
    public final SongPQ tail;

    public State(int position, Song[] planning, SongPQ tail) {
        this.position = position;
        this.planning = planning;
        this.tail = tail;
    }

    @Override
    public String toString() {
        return "State{" + "position=" + position + ", planning=" + Arrays.toString(planning) + ", tail=" + tail + '}';
    }

}
