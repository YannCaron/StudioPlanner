/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.algorithm;

import com.emacours.planner.model.DataModel;
import com.emacours.planner.model.Song;
import com.emacours.planner.model.Studio;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author cyann
 */
public class SongPlanner {

    private final DataModel model;
    private final CompatibilityGraph compatibilityGraph;
    private final Stack<State> stack;
    private final Set<Planning> plannings;
    private boolean isOver;

    public boolean hasNext() {
        return !isOver;
    }

    public SongPlanner(DataModel model) {
        this.model = model;
        this.compatibilityGraph = new CompatibilityGraph(model.getSongs());
        this.stack = new Stack<>();
        this.plannings = new HashSet<>();
        this.isOver = false;
    }

    private boolean checkConstraints(Song[] planning, Song song, int position) {
        // check slot is free
        if (planning[position] != null) {
            return false;
        }

        // check prefered studio
        Studio studio = model.getStudios().get(position % model.getStudios().size());
        int slot = position / model.getStudios().size();

        if (song.hasPreferedStudio() && song.getPreferedStudio() != studio) {
            return false;
        }

        // check player set compatibility
        int studioFrom = slot * model.getStudios().size();
        int studioTo = (slot + 1) * model.getStudios().size();

        for (int s = studioFrom; s < studioTo; s++) {
            Song checkSong = planning[s];
            if (checkSong != null && !compatibilityGraph.query(song, checkSong)) {
                return false;
            }
        }

        return true;
    }

    private int getNextHypothesis(Song[] planning, Song song) {
        for (int p = 0; p < planning.length; p++) {
            if (planning[p] == null && checkConstraints(planning, song, p)) {
                return p;
            }
        }
        return -1;
    }

    public void compute() {
        stack.clear();

        MinConstrainingSongPQ initialList = new MinConstrainingSongPQ(model.getSongs());

        stack.push(new State(new Song[model.getDomainSize()], initialList));

        next();
    }

    public void next() {
        int count = 0;
        while (!stack.isEmpty()) {
            State state = stack.pop();
            PriorityQueue<Song> pq = new PriorityQueue<>(state.tail);
            //System.out.println("Visite: " + state);
            if (state.tail.isEmpty()) {
                Planning planning = new Planning(state.planning, model);

                if (!plannings.contains(planning)) {
                    plannings.add(planning);
                    System.out.println("Found solution in [" + count + "] steps");
                    System.out.println(planning);
                    return;
                }
            }
            count++;

            while (!pq.isEmpty()) {
                Song song = pq.poll();

                int h = getNextHypothesis(state.planning, song);
                if (h != -1) {
                    Song[] planning = state.planning.clone();
                    planning[h] = song;
                    PriorityQueue<Song> newPq = new PriorityQueue<>(state.tail);
                    newPq.remove(song);

                    stack.push(new State(planning, newPq));
                }

            }

        }

        this.isOver = true;
    }

}
