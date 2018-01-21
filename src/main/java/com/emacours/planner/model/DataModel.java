/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.simpleframework.xml.Element;
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
@Root(strict = false)
public class DataModel {

    @ElementList(inline = true, required = false)
    private ObservableList<Studio> studios;

    private final SimpleIntegerProperty maxSlotProperty;

    @ElementList(inline = true, required = false)
    private ObservableList<Player> players;

    @ElementList(inline = true, required = false)
    private ObservableList<Instrument> instruments;

    @ElementList(inline = true, required = false)
    private List<InstrumentPlayer> instrumentPlayers;

    @ElementList(inline = true, required = false)
    private ObservableList<Song> songs;

    public ObservableList<Studio> getStudios() {
        return studios;
    }

    @Element
    public int getMaxSlot() {
        return maxSlotProperty.get();
    }

    @Element
    public void setMaxSlot(int maxSlot) {
        this.maxSlotProperty.set(maxSlot);
    }

    public SimpleIntegerProperty getMaxSlotProperty() {
        return maxSlotProperty;
    }

    public ObservableList<Player> getPlayers() {
        return players;
    }

    public ObservableList<Instrument> getInstruments() {
        return instruments;
    }

    public List<InstrumentPlayer> getInstrumentPlayers() {
        return instrumentPlayers;
    }

    public ObservableList<Song> getSongs() {
        return songs;
    }

    public DataModel() {
        studios = FXCollections.observableArrayList();
        maxSlotProperty = new SimpleIntegerProperty();
        players = FXCollections.observableArrayList();
        instruments = FXCollections.observableArrayList();
        instrumentPlayers = new ArrayList<>();
        songs = FXCollections.observableArrayList();
    }

    public int getDomainSize() {
        return studios.size() + getMaxSlot();
    }

    public int getStudioDomainSize() {
        return studios.size();
    }

    public static DataModel buildDummy() {

        DataModel dataModel = new DataModel();

        Studio studioA = new Studio("A");
        Studio studioB = new Studio("B");

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

        Song mJhones = new Song("Mr Johns");
        Song otis = new Song("Otis Reading");
        Song melody = new Song("Melody Garbot");
        Song lofo = new Song("Lofofora");
        Song jmsn = new Song("JMSN");
        Song jamiroquai = new Song("Jamiroquai");
        Song elbow = new Song("Elbow");
        Song rhcp = new Song("Red Hot Chili Pepers");

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
        dataModel.getInstruments().addAll(Arrays.asList(bass, guitare, voide, drum, percusion));
        dataModel.getPlayers().addAll(Arrays.asList(steph, fred, yann, estelle, lionel, dummy1, dummy2));
        dataModel.getInstrumentPlayers().addAll(Arrays.asList(stephDrum, stephPercusion, fredGuitare, fredVoice, fredDrum, yannBass, yannVoice, lionelBass, estelleVoice, dummy1Bass, dummy2Guitare));
        dataModel.getSongs().addAll(Arrays.asList(lofo, otis, jmsn, rhcp));

        return dataModel;
    }

    public String toXML() {
        Strategy strategy = new CycleStrategy("_id", "_ref");
        Serializer serializer = new Persister(strategy);
        StringWriter sw = new StringWriter();
        try {
            serializer.write(this, sw);
            sw.flush();
        } catch (Exception ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sw.toString();
    }

    // generate XML
    public static void main(String[] args) throws Exception {
        Strategy strategy = new CycleStrategy("_id", "_ref");
        Serializer serializer = new Persister(strategy);
        serializer.write(buildDummy(), System.out);

    }

}
