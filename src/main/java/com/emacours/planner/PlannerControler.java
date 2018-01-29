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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
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
    private static final Song DUMMY_SONG = new Song("-");

    private static final Logger LOGGER = Logger.getLogger(PlannerControler.class.getName());
    DataModel model;

    @FXML
    private VBox mainLayout;

    @FXML
    private TableView<Studio> studioTable;

    @FXML
    private Button addStudioButton, deleteStudioButton;

    @FXML
    private TableView<Instrument> instrumentTable;

    @FXML
    private Button addInstrumentButton, deleteInstrumentButton;

    @FXML
    private TableView<Player> playerTable;

    @FXML
    private Button addPlayerButton, deletePlayerButton;

    @FXML
    private TableView<Song> songTable;

    @FXML
    private Button clipboardSongButton, addSongButton, deleteSongButton;

    @FXML
    private Spinner durationSpinner;

    @FXML
    private Button runButton;

    @FXML
    private TableView<Slot> planningTable;

    @FXML
    private Button clipboardPlanningButton;

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

        assignButtonAdd(addStudioButton, model.getStudios(), () -> new Studio("new"));
        assignButtonDelete(deleteStudioButton, model.getStudios(), studioTable.getSelectionModel());

        // instrument
        instrumentTable.setEditable(true);
        addEditableStringTableColumn("Name", instrumentTable, (t) -> t.getNameProperty(), (t, v) -> t.setName(v));
        instrumentTable.setItems(model.getInstruments());

        assignButtonAdd(addInstrumentButton, model.getInstruments(), () -> new Instrument("new"));
        assignButtonDelete(deleteInstrumentButton, model.getInstruments(), instrumentTable.getSelectionModel());

        buildSongTable();
        buildPlayerTable();
        buildPlanningTable();

    }

    private void buildSongTable() {
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
            }
        });

        assignButtonAdd(addSongButton, model.getSongs(), () -> new Song("new"));
        assignButtonDelete(deleteSongButton, model.getSongs(), songTable.getSelectionModel());

    }

    private void buildPlayerTable() {
        playerTable.setEditable(true);
        TableColumn<Player, String> firstNameColumn = addEditableStringTableColumn("First name", playerTable, (t) -> t.getFirstNameProperty(), (t, v) -> t.setFirstName(v));
        TableColumn<Player, String> lastNameColumn = addEditableStringTableColumn("Last name", playerTable, (t) -> t.getLastNameProperty(), (t, v) -> t.setLastName(v));
        addEditableBooleanTableColumn("Loose", playerTable, (t) -> t.getLooseProperty(), (t, v) -> t.setLoose(v));
        playerTable.setItems(model.getPlayers());

        Callback<TableColumn<Player, String>, TableCell<Player, String>> playerCellCallback = (TableColumn<Player, String> param) -> new TextFieldTableCell<Player, String>(new DefaultStringConverter()) {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);

                if (getTableRow() == null || getTableRow().getIndex() == -1 || getTableRow().getIndex() >= getTableView().getItems().size()) {
                    return;
                }

                Player player = getTableView().getItems().get(getTableRow().getIndex());
                if (player.getPlaySong() > 1) {
                    setStyle(NOTICE_STYLE);
                } else if (player.getPlaySong() == 1) {
                    setStyle(NOTICE_STYLE_LIGHT);
                } else {
                    setStyle("");
                }
            }
        };

        firstNameColumn.setCellFactory(playerCellCallback);
        lastNameColumn.setCellFactory(playerCellCallback);

        assignButtonAdd(addPlayerButton, model.getPlayers(), () -> new Player("new", "", false));
        assignButtonDelete(deletePlayerButton, model.getPlayers(), playerTable.getSelectionModel());

    }

    private void buildPlanningTable() {
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

        planningTable.getSelectionModel().getSelectedCells().addListener((Observable observable) -> {
            clearHighlights();

            applyPlanningSelectionInPlayer();

            if (planningTable.getSelectionModel().getSelectedCells() != null
                    && planningTable.getSelectionModel().getSelectedCells().size() == 1) {

                int idColumn = planningTable.getSelectionModel().getSelectedCells().get(0).getColumn();
                if (idColumn >= 1) {

                    Slot selectedSlot = planningTable.getSelectionModel().getSelectedItem();
                    Studio studio = (Studio) planningTable.getColumns().get(idColumn).getUserData();

                    planningTable.getItems().forEach((slot) -> slot.applyPossibleAction(planner.getCompatibilityGraph(), DUMMY_SONG, selectedSlot, studio));

                    planningTable.refresh();
                }
            }
        });

        EventHandler<KeyEvent> clearOnEscape = (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                clearHighlights();
            }
        };

        Platform.runLater(() -> planningTable.getScene().setOnKeyPressed(clearOnEscape));
        planningTable.setOnKeyPressed(clearOnEscape);
        playerTable.setOnKeyPressed(clearOnEscape);

        playerTable.getSelectionModel().getSelectedCells().addListener((Observable observable) -> {
            clearHighlights();
            Player player = playerTable.getSelectionModel().getSelectedItem();
            planningTable.getItems().forEach((slot1) -> {
                slot1.applyPlayer(player);
            });
            planningTable.refresh();
        });

    }

    private boolean applyPlanningSelectionInPlayer() {
        Map<Player, Integer> playingSong = new HashMap<>();

        ObservableList<TablePosition> selection = planningTable.getSelectionModel().getSelectedCells();
        for (TablePosition position : selection) {

            if (position.getColumn() >= 1) {
                Slot selectedSlot = planningTable.getItems().get(position.getRow());
                Studio studio = (Studio) planningTable.getColumns().get(position.getColumn()).getUserData();

                Song song = selectedSlot.getSong(studio);
                for (Player player : song.getPlayers()) {
                    if (!playingSong.containsKey(player)) {
                        playingSong.put(player, 0);
                    }

                    playingSong.put(player, playingSong.get(player) + 1);
                }
            }

        }

        for (Player player : playerTable.getItems()) {
            player.applyPlaySong(playingSong);
        }
        playerTable.refresh();

        return true;
    }

    private void clearHighlights() {
        planningTable.getItems().forEach((com.emacours.planner.model.Slot slot1) -> slot1.clearPossibleAction());
        planningTable.refresh();
        playerTable.getItems().forEach((p) -> p.clearPlaySong());
        playerTable.refresh();
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

    private <T> void assignButtonAdd(Button button, ObservableList<T> list, Supplier<T> newInstance) {
        button.setOnMouseClicked((event) -> {
            list.add(newInstance.get());
        });
    }

    private <T> void assignButtonDelete(Button button, ObservableList<T> list, TableView.TableViewSelectionModel<T> selectionModel) {
        button.setOnMouseClicked((event) -> {
            list.remove(selectionModel.getSelectedItem());
        });
    }

    private void runPlanner() {
        planner = new SongPlanner(model);
        planner.compute();

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
                    song = DUMMY_SONG;
                }
                slot.addSong(model.getStudios().get(s), song);

                i++;
            }

            planningTable.getItems().clear();
            planningTable.getItems().addAll(slots);

            mainPane.getSelectionModel().select(planningTab);

        }

    }

}
