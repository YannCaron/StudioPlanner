/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.algorithm;

import com.emacours.planner.model.DataModel;
import com.emacours.planner.model.Player;
import com.emacours.planner.model.Song;
import com.emacours.planner.model.Studio;
import java.util.Arrays;
import java.util.HashSet;
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
    private int stepCount = 0;
    private boolean isOver;

    public boolean hasNext() {
        return !isOver;
    }

    public CompatibilityGraph getCompatibilityGraph() {
        return compatibilityGraph;
    }

    public int getStepCount() {
        return stepCount;
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

        Set<Player> loosePlayers = new HashSet<>();
        for (int s = studioFrom; s < studioTo; s++) {
            Song checkSong = planning[s];
            if (checkSong != null) {
                if (!compatibilityGraph.query(song, checkSong)) {
                    return false;
                } else {
                    Set<Player> otherLoose = compatibilityGraph.queryLoose(checkSong);
                    if (include(loosePlayers, otherLoose)) {
                        return false;
                    }

                    addAllSet(loosePlayers, otherLoose);
                }
            }
        }

        return true;
    }

    private static <T> void addAllSet(Set<T> to, Set<T> from) {
        if (from != null) {
            to.addAll(from);
        }
    }

    private static <T> boolean include(Set<T> set1, Set<T> set2) {
        if (set1 == null || set2 == null) {
            return false;
        }
        return set1.containsAll(set2);
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
        System.out.println("Compatibilities\n" + compatibilityGraph);

        stack.clear();

        SongPQ initialList = new SongPQ(model.getSongs());

        stack.push(new State(0, new Song[model.getDomainSize()], initialList));
    }

    public Planning next() {
        while (!stack.isEmpty()) {
            State state = stack.pop();
            SongPQ pq = new SongPQ(state.tail);
            System.out.println("Visite: " + Arrays.toString(state.planning));
            if (state.tail.isEmpty()) {
                Planning planning = new Planning(state.planning, model);

                if (!plannings.contains(planning)) {
                    plannings.add(planning);
                    System.out.println("Found solution in [" + stepCount + "] steps");
                    System.out.println(planning);
                    return planning;
                }
            }
            stepCount++;

            while (!pq.isEmpty()) {
                Song song = pq.poll();

                int h = getNextHypothesis(state.planning, song);
                if (h != -1) {
                    Song[] planning = state.planning.clone();
                    planning[h] = song;
                    SongPQ newPq = new SongPQ(state.tail);
                    newPq.remove(song);

                    stack.push(new State(0, planning, newPq));
                }

            }

        }
        this.isOver = true;
        System.out.println("No more solution found!");
        return null;
    }

}
