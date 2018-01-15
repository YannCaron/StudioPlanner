/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

import com.emacours.planner.model.Instrument;
import com.emacours.planner.model.Person;
import com.emacours.planner.model.Player;
import com.emacours.planner.model.Song;
import com.emacours.planner.model.Studio;
import com.emacours.planner.model.TimeSlot;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author formation
 */
public class Main {
    
    private static final Instrument BASS = new Instrument("bass");
    private static final Instrument GUITARE = new Instrument("guitare");
    private static final Instrument VOICE = new Instrument("voice");
    private static final Instrument DRUM = new Instrument("drum");
    private static final Instrument PERCUSION = new Instrument("percusion");
    
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
    
    private static final Player STEPH_DRUM = new Player(STEPH, DRUM);
    private static final Player STEPH_PERCUSION = new Player(STEPH, PERCUSION);
    private static final Player FRED_GUITARE = new Player(FRED, GUITARE);
    private static final Player FRED_VOICE = new Player(FRED, VOICE);
    private static final Player FRED_DRUM = new Player(FRED, DRUM);
    private static final Player YANN_BASS = new Player(YANN, BASS);
    private static final Player YANN_VOICE = new Player(YANN, BASS);
    private static final Player LIONEL_BASS = new Player(LIONEL, BASS);
    private static final Player ESTELLE_VOICE = new Player(ESTELLE, VOICE);
    private static final Player DUMMY1_BASS = new Player(DUMMY1, BASS);
    private static final Player DUMMY2_GUITARE = new Player(DUMMY2, GUITARE);
    
    private static final List<Studio> STUDIOS = new ArrayList<>();
    private static final List<TimeSlot> SLOTS = new ArrayList<>();
    private static final List<Song> SONGS = new ArrayList<>();
    
    {
        LOFO.addPlayer(YANN_VOICE);
        LOFO.addPlayer(FRED_DRUM);
        LOFO.addPlayer(STEPH_PERCUSION);
        LOFO.addPlayer(DUMMY1_BASS);
        LOFO.addPlayer(DUMMY2_GUITARE);
        
        OTIS.addPlayer(FRED_GUITARE);
        OTIS.addPlayer(YANN_BASS);
        
        JMSN.addPlayer(LIONEL_BASS);
        JMSN.addPlayer(ESTELLE_VOICE);
        
        //RHCP.addPlayer(FRED_VOICE);
        RHCP.addPlayer(DUMMY1_BASS);
        
        STUDIOS.add(new Studio("Studio A"));
        STUDIOS.add(new Studio("Studio B"));
        
        SLOTS.add(new TimeSlot("14h - 14h45"));
        SLOTS.add(new TimeSlot("14h45 - 16h"));
        
        SONGS.add(LOFO);
        SONGS.add(OTIS);
        SONGS.add(JMSN);
        SONGS.add(RHCP);
    }
    
    public static void main(String[] args) {
        System.out.println(YANN_VOICE);
    }
    
}
