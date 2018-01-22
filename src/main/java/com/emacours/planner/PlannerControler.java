/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

import com.emacours.planner.algorithm.SongPlanner;
import com.emacours.planner.model.DataModel;
import com.emacours.planner.model.Instrument;
import com.emacours.planner.model.Player;
import com.emacours.planner.model.Song;
import com.emacours.planner.model.Studio;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

/**
 *
 * @author cyann
 */
public class PlannerControler implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(PlannerControler.class.getName());
    DataModel model;

    @FXML
    private TableView<Studio> studioTable;

    @FXML
    private Button addStudioButton;

    @FXML
    private Button deleteStudioButton;

    @FXML
    private TableView<Instrument> instrumentTable;

    @FXML
    private Button addInstrumentButton;

    @FXML
    private Button deleteInstrumentButton;

    @FXML
    private TableView<Player> playerTable;

    @FXML
    private Button addPlayerButton;

    @FXML
    private Button deletePlayerButton;

    @FXML
    private TableView<Song> songTable;

    @FXML
    private Button addSongButton;

    @FXML
    private Button deleteSongButton;

    @FXML
    private Spinner slotSpinner;

    @FXML
    private Spinner durationSpinner;

    @FXML
    private Button runButton;

    @FXML
    private Accordion accord;

    @FXML
    private TitledPane studioPane, playerPane;

    SongPlanner planner = null;

    public PlannerControler() {

        Strategy strategy = new CycleStrategy("_id", "_ref");
        Serializer serializer = new Persister(strategy);
        File source = new File("studio.xml");

        try {
            model = serializer.read(DataModel.class, source);
        } catch (Exception ex) {
            Logger.getLogger(PlannerControler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        accord.setExpandedPane(playerPane);

        slotSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 25, 5));
        slotSpinner.getValueFactory().valueProperty().bindBidirectional(model.getMaxSlotProperty());

        runButton.setOnMouseClicked((event) -> {

            //System.out.println(model.toXML());
            try {
                Strategy strategy = new CycleStrategy("_id", "_ref");
                Serializer serializer = new Persister(strategy);
                File result = new File("studio.xml");
                serializer.write(model, result);
            } catch (Exception ex) {
                Logger.getLogger(PlannerControler.class.getName()).log(Level.SEVERE, null, ex);
            }

            planner = new SongPlanner(model);
            planner.compute();
            planner.next();

        });

        // studio
        studioTable.setEditable(true);
        addEditableStringTableColumn("Name", studioTable, (t) -> t.getNameProperty(), (t, v) -> t.setName(v));
        studioTable.setItems(model.getStudios());

        assignDataAdd(addStudioButton, model.getStudios(), () -> new Studio("new"));
        assignDataDelete(deleteStudioButton, model.getStudios(), studioTable.getSelectionModel());

        // instrument
        instrumentTable.setEditable(true);
        addEditableStringTableColumn("Name", instrumentTable, (t) -> t.getNameProperty(), (t, v) -> t.setName(v));
        instrumentTable.setItems(model.getInstruments());

        assignDataAdd(addInstrumentButton, model.getInstruments(), () -> new Instrument("new"));
        assignDataDelete(deleteInstrumentButton, model.getInstruments(), instrumentTable.getSelectionModel());

        // player
        playerTable.setEditable(true);
        addEditableStringTableColumn("First name", playerTable, (t) -> t.getFirstNameProperty(), (t, v) -> t.setFirstName(v));
        addEditableStringTableColumn("Last name", playerTable, (t) -> t.getLastNameProperty(), (t, v) -> t.setLastName(v));
        addEditableBooleanTableColumn("Free", playerTable, (t) -> t.getFreeProperty(), (t, v) -> t.setFree(v));
        playerTable.setItems(model.getPlayers());

        assignDataAdd(addPlayerButton, model.getPlayers(), () -> new Player("new", ""));
        assignDataDelete(deletePlayerButton, model.getPlayers(), playerTable.getSelectionModel());

        // song
        songTable.setEditable(true);
        addEditableStringTableColumn("Name", songTable, (t) -> t.getNameProperty(), (t, v) -> t.setName(v));
        addEditableObjectTableColumn("Prefered studio", songTable, model.getStudios(), (t) -> t.getPreferedStudioProperty(), (t, v) -> t.setPreferedStudio(v));
        for (Instrument instrument : model.getInstruments()) {
            addEditableObjectTableColumn(instrument.getName(), songTable, model.getPlayers(), (t) -> t.getPlayerProperty(instrument), (t, v) -> t.addPlayer(instrument, v));
        }
        addEditableStringTableColumn("Remark", songTable, (t) -> t.getCommentProperty(), (t, v) -> t.setComment(v));

        songTable.setItems(model.getSongs());

        songTable.setOnKeyPressed((event) -> {
            if (songTable.getSelectionModel().getSelectedCells().size() <= 0) {
                return;
            }
            int row = songTable.getSelectionModel().getSelectedCells().get(0).getRow();
            int col = songTable.getSelectionModel().getSelectedCells().get(0).getColumn();
            if (col == 1) {
                songTable.getItems().get(row).setPreferedStudio(null);
            }
        });

        model.getInstruments().addListener((Change<? extends Instrument> c) -> {
            while (c.next()) {
                for (Instrument instrument : c.getAddedSubList()) {
                    addEditableObjectTableColumn(instrument.getName(), songTable, model.getPlayers(), (t) -> t.getPlayerProperty(instrument), (t, v) -> t.addPlayer(instrument, v));
                }

                for (Instrument instrument : c.getRemoved()) {
                }

                //System.out.println(c.getAddedSubList());
            }
        });

        assignDataAdd(addSongButton, model.getSongs(), () -> new Song("new"));
        assignDataDelete(deleteSongButton, model.getSongs(), songTable.getSelectionModel());

    }

    private <T> void addEditableStringTableColumn(String columnName, TableView<T> tableView,
            Function<T, SimpleStringProperty> propertyAccessor,
            BiConsumer<T, String> propertySetter) {

        TableColumn<T, String> column = new TableColumn(columnName);
        column.setMinWidth(100);
        column.setEditable(true);
        column.setCellValueFactory(cellData -> propertyAccessor.apply(cellData.getValue()));
        column.setCellFactory(TextFieldTableCell.<T>forTableColumn());
        column.setOnEditCommit(
                (TableColumn.CellEditEvent<T, String> t) -> {
                    propertySetter.accept(((T) t.getTableView().getItems()
                            .get(t.getTablePosition().getRow())), t.getNewValue());
                });

        tableView.getColumns().add(column);
    }

    private <T> void addEditableBooleanTableColumn(String columnName, TableView<T> tableView,
            Function<T, SimpleBooleanProperty> propertyAccessor,
            BiConsumer<T, Boolean> propertySetter) {

        TableColumn<T, Boolean> column = new TableColumn(columnName);
        column.setMinWidth(100);
        column.setEditable(true);
        column.setCellValueFactory(cellData -> propertyAccessor.apply(cellData.getValue()));
        column.setCellFactory(CheckBoxTableCell.<T>forTableColumn(column));
        column.setOnEditCommit(
                (TableColumn.CellEditEvent<T, Boolean> t) -> {
                    propertySetter.accept(((T) t.getTableView().getItems()
                            .get(t.getTablePosition().getRow())), t.getNewValue());
                });

        tableView.getColumns().add(column);
    }

    private <T, U> void addEditableObjectTableColumn(String columnName, TableView<T> tableView, ObservableList<U> list,
            Function<T, SimpleObjectProperty<U>> propertyAccessor,
            BiConsumer<T, U> propertySetter/*,
            Comparator<U> comparator*/) {

        TableColumn<T, U> column = new TableColumn(columnName);
        column.setMinWidth(100);
        column.setEditable(true);
        column.setCellValueFactory(cellData -> propertyAccessor.apply(cellData.getValue()));
        //FXCollections.sort(list, comparator);
        column.setCellFactory(ComboBoxTableCell.forTableColumn(list));
        column.setOnEditCommit(
                (TableColumn.CellEditEvent<T, U> t) -> {
                    propertySetter.accept(((T) t.getTableView().getItems()
                            .get(t.getTablePosition().getRow())), t.getNewValue());
                });

        tableView.getColumns().add(column);
    }

    private <T> void assignDataAdd(Button button, ObservableList<T> list, Supplier<T> newInstance) {
        button.setOnMouseClicked((event) -> {
            list.add(newInstance.get());
        });
    }

    private <T> void assignDataDelete(Button button, ObservableList<T> list, TableView.TableViewSelectionModel<T> selectionModel) {
        button.setOnMouseClicked((event) -> {
            list.remove(selectionModel.getSelectedItem());
        });
    }

}
