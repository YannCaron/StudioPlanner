/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

import com.emacours.planner.algorithm.Planning;
import com.emacours.planner.algorithm.SongPlanner;
import com.emacours.planner.model.DataModel;
import com.emacours.planner.model.Instrument;
import com.emacours.planner.model.Player;
import com.emacours.planner.model.Slot;
import com.emacours.planner.model.Song;
import com.emacours.planner.model.Studio;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

/**
 *
 * @author cyann
 */
public class PlannerControler implements Initializable {

    public static final String NOTICE_STYLE = "-fx-background-color:#428aa3ff";
    public static final String NOTICE_STYLE_LIGHT = "-fx-background-color:#b7deedff";

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
    private Spinner durationSpinner;

    @FXML
    private Button runButton;

    @FXML
    private TableView<Slot> planningTable;

    @FXML
    private Accordion accord;

    @FXML
    private TitledPane studioPane, playerPane;

    @FXML
    private TabPane mainPane;

    @FXML
    private Tab songTab, planningTab;

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

            runPlanner();

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
        TableColumn<Player, String> firstNameColumn = addEditableStringTableColumn("First name", playerTable, (t) -> t.getFirstNameProperty(), (t, v) -> t.setFirstName(v));
        TableColumn<Player, String> lastNameColumn = addEditableStringTableColumn("Last name", playerTable, (t) -> t.getLastNameProperty(), (t, v) -> t.setLastName(v));
        addEditableBooleanTableColumn("Loose", playerTable, (t) -> t.getLooseProperty(), (t, v) -> t.setLoose(v));
        playerTable.setItems(model.getPlayers());

        Callback<TableColumn<Player, String>, TableCell<Player, String>> playerCellCallback = new Callback<TableColumn<Player, String>, TableCell<Player, String>>() {
            @Override
            public TableCell<Player, String> call(TableColumn<Player, String> param) {
                return new TableCell<Player, String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);

                        if (getTableRow() == null || getTableRow().getIndex() == -1 || getTableRow().getIndex() >= getTableView().getItems().size()) {
                            return;
                        }

                        Player player = getTableView().getItems().get(getTableRow().getIndex());
                        if (player.isPlaySong()) {
                            setStyle(NOTICE_STYLE);
                        } else {
                            setStyle("");
                        }
                    }
                };
            }
        };

        firstNameColumn.setCellFactory(playerCellCallback);
        lastNameColumn.setCellFactory(playerCellCallback);

        assignDataAdd(addPlayerButton, model.getPlayers(), () -> new Player("new", "", false));
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

    private <T> TableColumn<T, String> addEditableStringTableColumn(String columnName, TableView<T> tableView,
            Function<T, SimpleStringProperty> propertyAccessor,
            BiConsumer<T, String> propertySetter) {

        TableColumn<T, String> column = new TableColumn(columnName);
        //column.setMinWidth(50);
        column.setEditable(true);
        column.setCellValueFactory(cellData -> propertyAccessor.apply(cellData.getValue()));
        column.setCellFactory(TextFieldTableCell.<T>forTableColumn());
        column.setOnEditCommit(
                (TableColumn.CellEditEvent<T, String> t) -> {
                    propertySetter.accept(((T) t.getTableView().getItems()
                            .get(t.getTablePosition().getRow())), t.getNewValue());
                });

        tableView.getColumns().add(column);
        return column;
    }

    private <T> TableColumn<T, String> addReadonlyStringTableColumn(String columnName, TableView<T> tableView,
            Function<T, SimpleStringProperty> propertyAccessor) {

        TableColumn<T, String> column = new TableColumn(columnName);
        //column.setMinWidth(100);
        column.setEditable(true);
        column.setCellValueFactory(cellData -> propertyAccessor.apply(cellData.getValue()));

        tableView.getColumns().add(column);
        return column;
    }

    private <T> TableColumn<T, Boolean> addEditableBooleanTableColumn(String columnName, TableView<T> tableView,
            Function<T, SimpleBooleanProperty> propertyAccessor,
            BiConsumer<T, Boolean> propertySetter) {

        TableColumn<T, Boolean> column = new TableColumn(columnName);
        column.setMinWidth(40);
        column.setMaxWidth(40);
        column.setEditable(true);
        column.setCellValueFactory(cellData -> propertyAccessor.apply(cellData.getValue()));
        column.setCellFactory(CheckBoxTableCell.<T>forTableColumn(column));
        column.setOnEditCommit(
                (TableColumn.CellEditEvent<T, Boolean> t) -> {
                    propertySetter.accept(((T) t.getTableView().getItems()
                            .get(t.getTablePosition().getRow())), t.getNewValue());
                });

        tableView.getColumns().add(column);
        return column;
    }

    private <T, U> TableColumn<T, U> addEditableObjectTableColumn(String columnName, TableView<T> tableView, ObservableList<U> list,
            Function<T, SimpleObjectProperty<U>> propertyAccessor,
            BiConsumer<T, U> propertySetter/*,
            Comparator<U> comparator*/) {

        TableColumn<T, U> column = new TableColumn(columnName);
        //column.setMinWidth(100);
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
        return column;
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

    private void runPlanner() {
        planner = new SongPlanner(model);
        planner.compute();

        Song dummySong = new Song("-");

        Planning planning = planner.next();
        if (planning != null) {
            List<Slot> slots = new ArrayList<Slot>();

            int i = 0;
            Slot slot = null;
            for (Song song : planning.getPlanning()) {
                int s = i % model.getStudioDomainSize();
                int t = i / model.getStudioDomainSize();

                if (s == 0) {
                    if (slot != null && !slot.isEmpty()) {
                        slots.add(slot);
                    }

                    slot = new Slot("Slot " + (t + 1));
                }

                if (song == null) {
                    song = dummySong;
                }
                slot.addSong(model.getStudios().get(s), song);

                i++;
            }

            planningTable.getColumns().clear();
            planningTable.getItems().clear();
            planningTable.getItems().addAll(slots);

            planningTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            planningTable.getSelectionModel().setCellSelectionEnabled(true);
            addReadonlyStringTableColumn("Name", planningTable, (t) -> new ReadOnlyStringWrapper(t.getName()));

            for (Studio studio : model.getStudios()) {
                TableColumn<Slot, String> column = addReadonlyStringTableColumn(studio.getName(), planningTable, (t) -> new ReadOnlyStringWrapper(t.getSong(studio).getName()));
                column.setUserData(studio);

                column.setCellFactory((TableColumn<Slot, String> param) -> new TableCell<Slot, String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);

                        if (getTableRow().getIndex() == -1 || getTableRow().getIndex() >= getTableView().getItems().size()) {
                            return;
                        }

                        Slot slot = getTableView().getItems().get(getTableRow().getIndex());
                        switch (slot.getPossibleAction(studio)) {
                            case NONE:
                                setStyle(NOTICE_STYLE);
                                break;
                            case REPLACE:
                                setStyle(NOTICE_STYLE_LIGHT);
                                break;
                            case SWITCH:
                                setStyle("");
                                break;
                        }
                    }
                });

            }

            planningTable.getSelectionModel().getSelectedCells().addListener(new InvalidationListener() {

                boolean state = false;

                @Override
                public void invalidated(Observable observable) {

                    int idColumn = planningTable.getSelectionModel().getSelectedCells().get(0).getColumn();
                    if (idColumn < 1) {
                        slots.forEach((slot) -> slot.clearPossibleAction());
                        planningTable.refresh();
                        playerTable.getItems().forEach((p) -> p.clearPlaySong());
                        playerTable.refresh();
                        return;
                    }

                    Slot selectedSlot = planningTable.getSelectionModel().getSelectedItem();
                    Studio studio = (Studio) planningTable.getColumns().get(idColumn).getUserData();
                    Song song = selectedSlot.getSong(studio);

                    planningTable.getItems().forEach((slot) -> slot.applyPossibleAction(planner.getCompatibilityGraph(), dummySong, selectedSlot, studio));

                    state = !state;
                    planningTable.refresh();

                    for (Player player : playerTable.getItems()) {
                        player.applyPlaySong(song);
                    }
                    playerTable.refresh();
                }
            });

            planningTable.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue == false) {
                        planningTable.getItems().forEach((slot) -> slot.clearPossibleAction());
                        planningTable.refresh();
                        playerTable.getItems().forEach((p) -> p.clearPlaySong());
                        playerTable.refresh();
                    }
                }
            });

            playerTable.getSelectionModel().getSelectedCells().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    Player player = playerTable.getSelectionModel().getSelectedItem();
                    for (Slot slot : slots) {
                        slot.applyPlayer(player);
                    }
                    planningTable.refresh();
                }
            });

            playerTable.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue == false) {
                        planningTable.getItems().forEach((slot) -> slot.clearPossibleAction());
                        planningTable.refresh();
                    }
                }
            });

            mainPane.getSelectionModel().select(planningTab);

        }

    }

}
