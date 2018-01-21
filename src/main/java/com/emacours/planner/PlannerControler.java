/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

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
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

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

    public PlannerControler() {

        Serializer serializer = new Persister();
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

        slotSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 5));
        slotSpinner.getValueFactory().valueProperty().bindBidirectional(model.getMaxSlotProperty());

        ///durationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 240, 45, 15));
        runButton.setOnMouseClicked((event) -> {
            System.out.println(model.getSongs().get(0));
            System.out.println(model.toXML());
/*
            Serializer serializer = new Persister();
            File result = new File("studio.xml");

            try {
                serializer.write(model, result);
            } catch (Exception ex) {
                Logger.getLogger(PlannerControler.class.getName()).log(Level.SEVERE, null, ex);
            }*/
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
        playerTable.setItems(model.getPlayers());

        assignDataAdd(addPlayerButton, model.getPlayers(), () -> new Player("new", ""));
        assignDataDelete(deletePlayerButton, model.getPlayers(), playerTable.getSelectionModel());

        // song
        songTable.setEditable(true);
        addEditableStringTableColumn("Name", songTable, (t) -> t.getNameProperty(), (t, v) -> t.setName(v));
        addEditableObjectTableColumn("Prefered studio", songTable, model.getStudios(), (t) -> t.getPreferedStudioProperty(), (t, v) -> t.setPreferedStudio(v));
        songTable.setItems(model.getSongs());

        assignDataAdd(addSongButton, model.getSongs(), () -> new Song("new", null));
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

    private <T, U> void addEditableObjectTableColumn(String columnName, TableView<T> tableView, ObservableList<U> list,
            Function<T, SimpleObjectProperty<U>> propertyAccessor,
            BiConsumer<T, U> propertySetter) {

        TableColumn<T, U> column = new TableColumn(columnName);
        column.setMinWidth(100);
        column.setEditable(true);
        column.setCellValueFactory(cellData -> propertyAccessor.apply(cellData.getValue()));
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

    public void setData() {
    }

}
