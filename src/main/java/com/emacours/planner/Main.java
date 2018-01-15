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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author formation
 */
public class Main {

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
    private static final List<Studio> STUDIOS = new ArrayList<>();
    private static final List<TimeSlot> SLOTS = new ArrayList<>();

    private static final void initializeWorld() {
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

        STUDIOS.add(new Studio("Studio A"));
        STUDIOS.add(new Studio("Studio B"));

        SLOTS.add(new TimeSlot("14h - 14h45"));
        SLOTS.add(new TimeSlot("14h45 - 16h"));

    }

    // TODO : to be optimized with a priority queue !
    public static Song getMaxConstraint(List<Song> songs) {
        assert songs.size() > 0;

        return songs.stream().max((Song o1, Song o2) -> {
            if (o1.countConstraint() < o2.countConstraint()) {
                return -1;
            } else if (o1.countConstraint() > o2.countConstraint()) {
                return 1;
            } else {
                return 0;
            }
        }).orElse(songs.get(0));

    }

    public static void printPlanning(Song[][] planning) {
        System.out.println("");
        System.out.println("Planning :");
        
        for (int slot = 0; slot < planning.length; slot++) {
            if (slot > 0) {
                System.out.println("---------------------------------------------------------------");
            }

            System.out.print(SLOTS.get(slot).getName());
            System.out.print(" | ");

            for (int stud = 0; stud < planning[slot].length; stud++) {
                if (stud > 0) {
                    System.out.print(" | ");
                }
                System.out.print(STUDIOS.get(stud).getName());
                System.out.print("(");
                System.out.print(planning[slot][stud]);
                System.out.print(")");
            }

            System.out.println("");
        }
        
        System.out.println("");
    }

    public static void main(String[] args) {
        initializeWorld();

        LinkedList<Song> remainingSong = new LinkedList<>();
        remainingSong.addAll(Arrays.asList(LOFO, OTIS, JMSN, RHCP));

        Song[][] planning = new Song[SLOTS.size()][STUDIOS.size()];

        printPlanning(planning);

        while (!remainingSong.isEmpty()) {
            Song candidate = getMaxConstraint(remainingSong);
            remainingSong.remove(candidate);
            System.out.println(candidate);
        }
    }

}
