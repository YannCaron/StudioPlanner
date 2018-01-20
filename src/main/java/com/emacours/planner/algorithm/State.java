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
    
    public final Song[] planning;
    public final PriorityQueue<Song> tail;

    public State(Song[] planning, PriorityQueue<Song> tail) {
        this.planning = planning;
        this.tail = tail;
    }

    @Override
    public String toString() {
        return "State{" + "planning=" + Arrays.toString(planning) + ", tail=" + tail + '}';
    }
    
}
