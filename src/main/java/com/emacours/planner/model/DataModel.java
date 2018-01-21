/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.model;

import java.io.StringWriter;
import java.util.Arrays;
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

    public ObservableList<Song> getSongs() {
        return songs;
    }

    public DataModel() {
        studios = FXCollections.observableArrayList();
        maxSlotProperty = new SimpleIntegerProperty();
        players = FXCollections.observableArrayList();
        instruments = FXCollections.observableArrayList();
        songs = FXCollections.observableArrayList();
    }

    public int getDomainSize() {
        return studios.size() * getMaxSlot();
    }

    public int getStudioDomainSize() {
        return studios.size();
    }

    public static DataModel buildDummy() {

        DataModel dataModel = new DataModel();

        Studio studioA = new Studio("A");
        Studio studioB = new Studio("B");
        dataModel.setMaxSlot(2);

        Instrument bass = new Instrument("bass");
        Instrument guitare = new Instrument("guitare");
        Instrument voice = new Instrument("voice");
        Instrument drum = new Instrument("drum");
        Instrument percusion = new Instrument("percusion");

        Player steph = new Player("Steph", "");
        Player fred = new Player("Fred", "");
        Player yann = new Player("Yann", "Caron");
        Player lionel = new Player("Lionel", "");
        Player estelle = new Player("Estelle", "Gardia");
        Player dummy1 = new Player("Dummy 1", "");
        Player dummy2 = new Player("Dummy 2", "");

        Song mJhones = new Song("Mr Johns");
        Song otis = new Song("Otis Reading");
        Song melody = new Song("Melody Garbot");
        Song lofo = new Song("Lofofora");
        Song jmsn = new Song("JMSN");
        Song jamiroquai = new Song("Jamiroquai");
        Song elbow = new Song("Elbow");
        Song rhcp = new Song("Red Hot Chili Pepers");

        lofo.setPreferedStudio(studioB);
        lofo.addPlayer(voice, yann);
        lofo.addPlayer(guitare, fred);
        lofo.addPlayer(percusion, steph);
        lofo.addPlayer(bass, dummy1);
        lofo.addPlayer(guitare, dummy2);

        otis.addPlayer(guitare, fred);
        otis.addPlayer(bass, yann);

        jmsn.addPlayer(bass, lionel);
        jmsn.addPlayer(voice, estelle);

        //RHCP.addPlayer(FRED);
        rhcp.addPlayer(bass, dummy1);

        // DataModel
        dataModel.getStudios().addAll(Arrays.asList(studioA, studioB));
        dataModel.getInstruments().addAll(Arrays.asList(bass, guitare, voice, drum, percusion));
        dataModel.getPlayers().addAll(Arrays.asList(steph, fred, yann, estelle, lionel, dummy1, dummy2));
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
