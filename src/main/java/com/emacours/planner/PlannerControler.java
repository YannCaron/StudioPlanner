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
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    private DataModel model;
    private SongPlanner planner = null;
    private Stage stage;
    private Scene scene;
    private final Map<Instrument, TableColumn> songColumns = new HashMap<>();
    private final Map<Studio, TableColumn> planningColumns = new HashMap<>();

    @FXML
    private MenuItem newMenu, openMenu, saveMenu, saveAsMenu, revertMenu, quitMenu, aboutMenu;

    @FXML
    private Label leftLabel, rightLabel;

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

    public DataModel getModel() {
        return model;
    }

    public PlannerControler() {
        this.model = new DataModel();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        accord.setExpandedPane(playerPane);

        // studio
        studioTable.setEditable(true);
        addEditableStringTableColumn("Name", studioTable, (t) -> t.getNameProperty(), (t, v) -> t.setName(v));

        // instrument
        instrumentTable.setEditable(true);
        addEditableStringTableColumn("Name", instrumentTable, (t) -> t.getNameProperty(), (t, v) -> t.setName(v));

        // song
        songTable.setEditable(true);
        addEditableStringTableColumn("Name", songTable, (t) -> t.getNameProperty(), (t, v) -> t.setName(v));
        addEditableObjectTableColumn("Prefered studio", songTable, model.getStudios(), (t) -> t.getPreferedStudioProperty(), (t, v) -> t.setPreferedStudio(v));
        addEditableStringTableColumn("Remark", songTable, (t) -> t.getCommentProperty(), (t, v) -> t.setComment(v));

        // player
        playerTable.setEditable(true);
        TableColumn<Player, String> firstNameColumn = addEditableStringTableColumn("First name", playerTable, (t) -> t.getFirstNameProperty(), (t, v) -> t.setFirstName(v));
        TableColumn<Player, String> lastNameColumn = addEditableStringTableColumn("Last name", playerTable, (t) -> t.getLastNameProperty(), (t, v) -> t.setLastName(v));
        addEditableBooleanTableColumn("Loose", playerTable, (t) -> t.getLooseProperty(), (t, v) -> t.setLoose(v));

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

        // planning
        planningTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        planningTable.getSelectionModel().setCellSelectionEnabled(true);

    }

    public void initializeEvents(Stage stage, Scene scene) {

        this.stage = stage;
        this.scene = scene;

        newMenu.setOnAction(this::newMenu_onAction);
        openMenu.setOnAction(this::openMenu_onAction);
        saveMenu.setOnAction(this::saveMenu_onAction);
        saveAsMenu.setOnAction(this::saveAsMenu_onAction);
        revertMenu.setOnAction(this::revertMenu_onAction);
        quitMenu.setOnAction(this::quitMenu_onAction);
        aboutMenu.setOnAction(this::aboutMenu_onAction);
        runButton.setOnMouseClicked(this::runButton_onClick);

        scene.setOnKeyPressed(this::this_onEscape);
        planningTable.setOnKeyPressed(this::this_onEscape);
        playerTable.setOnKeyPressed(this::this_onEscape);

        assignButtonAdd(addStudioButton, () -> model.getStudios(), () -> new Studio("new"));
        assignButtonDelete(deleteStudioButton, () -> model.getStudios(), studioTable.getSelectionModel());

        assignButtonAdd(addInstrumentButton, () -> model.getInstruments(), () -> new Instrument("new"));
        assignButtonDelete(deleteInstrumentButton, () -> model.getInstruments(), instrumentTable.getSelectionModel());

        assignButtonAdd(addSongButton, () -> model.getSongs(), () -> new Song("new"));
        assignButtonDelete(deleteSongButton, () -> model.getSongs(), songTable.getSelectionModel());

        assignButtonAdd(addPlayerButton, () -> model.getPlayers(), () -> new Player("new", "", false));
        assignButtonDelete(deletePlayerButton, () -> model.getPlayers(), playerTable.getSelectionModel());

        songTable.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (songTable.getSelectionModel().getSelectedCells().size() <= 0) {
                    return;
                }
                int row = songTable.getSelectionModel().getSelectedCells().get(0).getRow();
                int col = songTable.getSelectionModel().getSelectedCells().get(0).getColumn();
                if (col == 1) {
                    songTable.getItems().get(row).setPreferedStudio(null);
                }
            }
        });

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

        playerTable.getSelectionModel().getSelectedCells().addListener((Observable observable) -> {
            clearHighlights();
            Player player = playerTable.getSelectionModel().getSelectedItem();
            planningTable.getItems().forEach((slot1) -> {
                slot1.applyPlayer(player);
            });
            planningTable.refresh();
        });

        loadModel();

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

    private void loadModel() {
        studioTable.setItems(model.getStudios());
        instrumentTable.setItems(model.getInstruments());
        clearSongColumns();
        createSongColumns();
        songTable.setItems(model.getSongs());
        playerTable.setItems(model.getPlayers());

        clearPlanning();

        model.getInstruments().addListener(this::instruments_onChange);
    }

    private <T> void assignButtonAdd(Button button, Supplier<ObservableList<T>> getList, Supplier<T> newInstance) {
        button.setOnMouseClicked((event) -> {
            getList.get().add(newInstance.get());
        });
    }

    private <T> void assignButtonDelete(Button button, Supplier<ObservableList<T>> getList, TableView.TableViewSelectionModel<T> selectionModel) {
        button.setOnMouseClicked((event) -> {
            getList.get().remove(selectionModel.getSelectedItem());
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

            createPlanningColumns();
            planningTable.getItems().clear();
            planningTable.getItems().addAll(slots);

            mainPane.getSelectionModel().select(planningTab);

        }

    }

    private void clearSongColumns() {
        for (Instrument instrument : songColumns.keySet()) {
            songTable.getColumns().remove(songColumns.get(instrument));
        }
        songColumns.clear();
    }

    private void createSongColumns() {
        for (Instrument instrument : model.getInstruments()) {
            TableColumn col = addEditableObjectTableColumn(instrument.getName(), songTable, model.getPlayers(), (t) -> t.getPlayerProperty(instrument), (t, v) -> t.addPlayer(instrument, v));
            songColumns.put(instrument, col);
        }
    }

    private void clearPlanning() {
        planningTable.getColumns().clear();
        planningTable.getItems().clear();
    }

    private void createPlanningColumns() {
        clearPlanning();

        addReadonlyStringTableColumn("Time slot", planningTable, (t) -> new ReadOnlyStringWrapper(t.getName()));

        for (Studio studio : model.getStudios()) {

            TableColumn<Slot, String> col = addReadonlyStringTableColumn(studio.getName(), planningTable, (t) -> new ReadOnlyStringWrapper(t.getSong(studio).getName()));
            col.setUserData(studio);
            col.setCellFactory((TableColumn<Slot, String> param) -> new TableCell<Slot, String>() {

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

            planningColumns.put(studio, col);
        }
    }

    protected void newMenu_onAction(ActionEvent event) {
        this.model = new DataModel(this.model);
        loadModel();
    }

    protected void openMenu_onAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Planner File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EMA studio planner file", "*.emaml"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {

            Strategy strategy = new CycleStrategy("_id", "_ref");
            Serializer serializer = new Persister(strategy);

            try {
                this.model = serializer.read(DataModel.class, file);
                loadModel();
            } catch (Exception ex) {
                Logger.getLogger(PlannerControler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    protected void saveMenu_onAction(ActionEvent event) {
        try {
            Strategy strategy = new CycleStrategy("_id", "_ref");
            Serializer serializer = new Persister(strategy);
            File result = new File("studio.xml");
            serializer.write(model, result);
        } catch (Exception ex) {
            Logger.getLogger(PlannerControler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    protected void saveAsMenu_onAction(ActionEvent event) {

    }

    protected void revertMenu_onAction(ActionEvent event) {

    }

    protected void quitMenu_onAction(ActionEvent event) {

    }

    protected void aboutMenu_onAction(ActionEvent event) {

    }

    protected void runButton_onClick(MouseEvent event) {
        runPlanner();
    }

    protected void this_onEscape(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            clearHighlights();
        }
    }

    protected void instruments_onChange(Change<? extends Instrument> change) {
        System.out.println("CHANGED");
        while (change.next()) {

            for (Instrument instrument : change.getAddedSubList()) {
                TableColumn col = addEditableObjectTableColumn(instrument.getName(), songTable, getModel().getPlayers(), (t) -> t.getPlayerProperty(instrument), (t, v) -> t.addPlayer(instrument, v));
                songColumns.put(instrument, col);
            }

            for (Instrument instrument : change.getRemoved()) {
                TableColumn col = songColumns.get(instrument);
                songColumns.remove(instrument);
                songTable.getColumns().remove(col);
            }
        }
    }

}
