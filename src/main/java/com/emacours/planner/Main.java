/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

import com.emacours.planner.model.Person;
import com.emacours.planner.model.Song;
import com.emacours.planner.model.Studio;
import com.emacours.planner.model.TimeSlot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author formation
 */
public class Main {

    private static class State {

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

    /*
    private static final Instrument BASS = new Instrument("bass");
    private static final Instrument GUITARE = new Instrument("guitare");
    private static final Instrument VOICE = new Instrument("voice");
    private static final Instrument DRUM = new Instrument("drum");
    private static final Instrument PERCUSION = new Instrument("percusion");
     */
    private static final Song M_JOHNS = new Song("Mr Johns", "Counting crows");
    private static final Song OTIS = new Song("Hard to handle", "Otis Reading");
    private static final Song MELODY = new Song("Who will comfort me", "Melody Garbot");
    private static final Song LOFO = new Song("Le fond et la forme", "Lofofora");
    private static final Song JMSN = new Song("Drinkin", "JMSN");
    private static final Song JAMI = new Song("Time won't wait", "Jamiroquai");
    private static final Song ELBOW = new Song("Magnificient", "Elbow");
    private static final Song RHCP = new Song("Parallele Universe", "Red Hot Chili Pepers");

    private static final Person STEPH = new Person("Steph", "");
    private static final Person FRED = new Person("Fred", "");
    private static final Person YANN = new Person("Yann", "Caron");
    private static final Person ESTELLE = new Person("Estelle", "Gardia");
    private static final Person LIONEL = new Person("Lionel", "");
    private static final Person DUMMY1 = new Person("Dummy 1", "");
    private static final Person DUMMY2 = new Person("Dummy 2", "");
    /*
    private static final Player STEPH_DRUM = new Player(STEPH, DRUM);
    private static final Player STEPH_PERCUSION = new Player(STEPH, PERCUSION);
    private static final Player FRED_GUITARE = new Player(FRED, GUITARE);
    private static final Player FRED_VOICE = new Player(FRED, VOICE);
    private static final Player FRED_DRUM = new Player(FRED, DRUM);
    private static final Player YANN_BASS = new Player(YANN, BASS);
    private static final Player YANN_VOICE = new Player(YANN, VOICE);
    private static final Player LIONEL_BASS = new Player(LIONEL, BASS);
    private static final Player ESTELLE_VOICE = new Player(ESTELLE, VOICE);
    private static final Player DUMMY1_BASS = new Player(DUMMY1, BASS);
    private static final Player DUMMY2_GUITARE = new Player(DUMMY2, GUITARE);
     */
    private static final Studio STUDIO_A = new Studio("Studio A");
    private static final Studio STUDIO_B = new Studio("Studio B");
    private static final List<Studio> STUDIOS = new ArrayList<>();
    private static final List<TimeSlot> SLOTS = new ArrayList<>();
    private static final List<Song> SONGS = new ArrayList<>();
    private static int DOMAIN_LENGTH;

    private static final Map<Song, Set<Song>> COMPATIBILITY = new HashMap<>();

    private static boolean areCompatible(Song song1, Song song2) {
        if (song1 == song2) {
            return false;
        }
        for (Person person : song1.getPlayers()) {
            if (song2.containsPlayer(person)) {
                return false;
            }
        }
        return true;
    }

    private static void initializeWorld() {

        STUDIOS.add(STUDIO_A);
        STUDIOS.add(STUDIO_B);

        SLOTS.add(new TimeSlot("14h - 14h45"));
        SLOTS.add(new TimeSlot("14h45 - 16h"));
        //SLOTS.add(new TimeSlot("16h - 16h45"));

        DOMAIN_LENGTH = STUDIOS.size() * SLOTS.size();

        LOFO.setPreferedStudio(STUDIO_B);
        LOFO.addPlayer(YANN);
        LOFO.addPlayer(FRED);
        LOFO.addPlayer(STEPH);
        LOFO.addPlayer(DUMMY1);
        LOFO.addPlayer(DUMMY2);

        OTIS.addPlayer(FRED);
        OTIS.addPlayer(YANN);

        JMSN.addPlayer(LIONEL);
        JMSN.addPlayer(ESTELLE);

        //RHCP.addPlayer(FRED);
        RHCP.addPlayer(DUMMY1);

        SONGS.addAll(Arrays.asList(LOFO, OTIS, JMSN, RHCP));

        // build compatibility
        for (Song song1 : SONGS) {
            Set<Song> set = new HashSet<>();
            for (Song song2 : SONGS) {
                if (areCompatible(song1, song2)) {
                    set.add(song2);
                }
                COMPATIBILITY.put(song1, set);
            }
        }

        System.out.println(COMPATIBILITY);

    }

    private static final Comparator<Song> MIN_CONSTRAINT_SONG = (Song o1, Song o2) -> {
        if (o1.countConstraint() < o2.countConstraint()) {
            return -1;
        } else if (o1.countConstraint() > o2.countConstraint()) {
            return 1;
        } else {
            return 0;
        }
    };

    //public static final 
    public static boolean checkConstraints(Song[] planning, Song song, int position) {
        // check slot is free
        if (planning[position] != null) {
            return false;
        }

        // check prefered studio
        Studio studio = STUDIOS.get(position % STUDIOS.size());
        int slot = position / STUDIOS.size();

        if (song.hasPreferedStudio() && song.getPreferedStudio() != studio) {
            return false;
        }

        // check player set compatibility
        int studioFrom = slot * STUDIOS.size();
        int studioTo = (slot + 1) * STUDIOS.size();
        Set<Song> compatibleSongs = COMPATIBILITY.get(song);

        for (int s = studioFrom; s < studioTo; s++) {
            Song checkSong = planning[s];
            if (checkSong != null && !compatibleSongs.contains(checkSong)) {
                return false;
            }
        }

        return true;
    }

    public static int getNextHypothesis(Song[] planning, Song song) {
        for (int p = 0; p < planning.length; p++) {
            if (planning[p] == null && checkConstraints(planning, song, p)) {
                return p;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        initializeWorld();

        Stack<State> stack = new Stack<>();

        PriorityQueue<Song> initialList = new PriorityQueue<>(MIN_CONSTRAINT_SONG);
        initialList.addAll(SONGS);

        stack.push(new State(new Song[DOMAIN_LENGTH], initialList));

        int count = 0;
        while (!stack.isEmpty()) {
            State state = stack.pop();
            PriorityQueue<Song> pq = new PriorityQueue<>(state.tail);
            System.out.println("Visite: " + state);
            if (state.tail.isEmpty()) {
                System.out.println("Found solution in [" + count + "] steps : " + state);
                return;
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

        System.out.println("No solution found ! ");

    }

}
