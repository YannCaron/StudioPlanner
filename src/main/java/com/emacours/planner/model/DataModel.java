/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

/**
 *
 * @author cyann
 */
@Root
public class DataModel {

    @ElementList(inline = true)
    private ObservableList<Studio> studios;

    @ElementList(inline = true)
    private List<TimeSlot> slots;

    @ElementList(inline = true)
    private List<Player> players;

    @ElementList(inline = true)
    private List<Instrument> instruments;

    @ElementList(inline = true)
    private List<InstrumentPlayer> instrumentPlayers;

    @ElementList(inline = true)
    private List<Song> songs;

    public ObservableList<Studio> getStudios() {
        return studios;
    }

    public List<TimeSlot> getSlots() {
        return slots;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Instrument> getInstruments() {
        return instruments;
    }

    public List<InstrumentPlayer> getInstrumentPlayers() {
        return instrumentPlayers;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public DataModel() {
        studios = FXCollections.observableArrayList();
        slots = new ArrayList<>();
        players = new ArrayList<>();
        instruments = new ArrayList<>();
        instrumentPlayers = new ArrayList<>();
        songs = new ArrayList<>();
    }

    public int getDomainSize() {
        return studios.size() + slots.size();
    }
    
    public int getStudioDomainSize() {
        return studios.size();
    }

    public static DataModel buildDummy() {

        DataModel dataModel = new DataModel();

        Studio studioA = new Studio("A");
        Studio studioB = new Studio("B");

        TimeSlot slot1 = new TimeSlot("Slot 1 (14h - 14h45)");
        TimeSlot slot2 = new TimeSlot("Slot 2 (14h45 - 15h30)");

        Instrument bass = new Instrument("bass");
        Instrument guitare = new Instrument("guitare");
        Instrument voide = new Instrument("voice");
        Instrument drum = new Instrument("drum");
        Instrument percusion = new Instrument("percusion");

        Player steph = new Player("Steph", "");
        Player fred = new Player("Fred", "");
        Player yann = new Player("Yann", "Caron");
        Player lionel = new Player("Lionel", "");
        Player estelle = new Player("Estelle", "Gardia");
        Player dummy1 = new Player("Dummy 1", "");
        Player dummy2 = new Player("Dummy 2", "");

        InstrumentPlayer stephDrum = new InstrumentPlayer(steph, drum);
        InstrumentPlayer stephPercusion = new InstrumentPlayer(steph, percusion);
        InstrumentPlayer fredGuitare = new InstrumentPlayer(fred, guitare);
        InstrumentPlayer fredVoice = new InstrumentPlayer(fred, voide);
        InstrumentPlayer fredDrum = new InstrumentPlayer(fred, drum);
        InstrumentPlayer yannBass = new InstrumentPlayer(yann, bass);
        InstrumentPlayer yannVoice = new InstrumentPlayer(yann, voide);
        InstrumentPlayer lionelBass = new InstrumentPlayer(lionel, bass);
        InstrumentPlayer estelleVoice = new InstrumentPlayer(estelle, voide);
        InstrumentPlayer dummy1Bass = new InstrumentPlayer(dummy1, bass);
        InstrumentPlayer dummy2Guitare = new InstrumentPlayer(dummy2, guitare);

        Song mJhones = new Song("Mr Johns", "Counting crows");
        Song otis = new Song("Hard to handle", "Otis Reading");
        Song melody = new Song("Who will comfort me", "Melody Garbot");
        Song lofo = new Song("Le fond et la forme", "Lofofora");
        Song jmsn = new Song("Drinkin", "JMSN");
        Song jamiroquai = new Song("Time won't wait", "Jamiroquai");
        Song elbow = new Song("Magnificient", "Elbow");
        Song rhcp = new Song("Parallele Universe", "Red Hot Chili Pepers");

        lofo.setPreferedStudio(studioB);
        lofo.addPlayer(yannVoice);
        lofo.addPlayer(fredGuitare);
        lofo.addPlayer(stephPercusion);
        lofo.addPlayer(dummy1Bass);
        lofo.addPlayer(dummy2Guitare);

        otis.addPlayer(fredGuitare);
        otis.addPlayer(yannBass);

        jmsn.addPlayer(lionelBass);
        jmsn.addPlayer(estelleVoice);

        //RHCP.addPlayer(FRED);
        rhcp.addPlayer(dummy1Bass);

        // DataModel
        dataModel.getStudios().addAll(Arrays.asList(studioA, studioB));
        dataModel.getSlots().addAll(Arrays.asList(slot1, slot2));
        dataModel.getInstruments().addAll(Arrays.asList(bass, guitare, voide, drum, percusion));
        dataModel.getPlayers().addAll(Arrays.asList(steph, fred, yann, estelle, lionel, dummy1, dummy2));
        dataModel.getInstrumentPlayers().addAll(Arrays.asList(stephDrum, stephPercusion, fredGuitare, fredVoice, fredDrum, yannBass, yannVoice, lionelBass, estelleVoice, dummy1Bass, dummy2Guitare));
        dataModel.getSongs().addAll(Arrays.asList(lofo, otis, jmsn, rhcp));

        return dataModel;
    }

    // generate XML
    public static void main(String[] args) throws Exception {
        Strategy strategy = new CycleStrategy("_id", "_ref");
        Serializer serializer = new Persister(strategy);
        serializer.write(buildDummy(), System.out);

    }

}
