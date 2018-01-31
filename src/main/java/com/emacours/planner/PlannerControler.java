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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

/**
 *
 * @author cyann
 */
public class PlannerControler implements Constants, Initializable {
    
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
    private Button addSongButton, deleteSongButton, clearSongButton, clipboardSongButton, printSongButton;
    
    @FXML
    private Spinner durationSpinner;
    
    @FXML
    private Button runButton;
    
    @FXML
    private TableView<Slot> planningTable;
    
    @FXML
    private Button clipboardPlanningButton, printPlanningButton;
    
    @FXML
    private Accordion accord;
    
    @FXML
    private TitledPane studioPane, playerPane;
    
    @FXML
    private TabPane mainPane;
    
    @FXML
    private Tab songTab, planningTab;
    
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
        songTable.getSelectionModel().setCellSelectionEnabled(true);
        addEditableStringTableColumn("Name", songTable, (t) -> t.getNameProperty(), (t, v) -> t.setName(v));
        addEditableStringTableColumn("Remark", songTable, (t) -> t.getCommentProperty(), (t, v) -> t.setComment(v));

        // player
        playerTable.setEditable(true);
        TableColumn<Player, String> firstNameColumn = addEditableStringTableColumn("First name", playerTable, (t) -> t.getFirstNameProperty(), (t, v) -> {
            t.setFirstName(v);
            songTable.refresh();
        });
        TableColumn<Player, String> lastNameColumn = addEditableStringTableColumn("Last name", playerTable, (t) -> t.getLastNameProperty(), (t, v) -> {
            t.setLastName(v);
            songTable.refresh();
        });
        addEditableBooleanTableColumn("Loose", playerTable, (t) -> t.getLooseProperty(), (t, v) -> t.setLoose(v));
        
        firstNameColumn.setCellFactory(PlayerEditCell.forTableColumn());
        lastNameColumn.setCellFactory(PlayerEditCell.forTableColumn());

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
        assignButtonDelete(deleteStudioButton, () -> model.getStudios(), studioTable.getSelectionModel(), (s) -> true);
        
        assignButtonAdd(addInstrumentButton, () -> model.getInstruments(), () -> new Instrument("new"));
        assignButtonDelete(deleteInstrumentButton, () -> model.getInstruments(), instrumentTable.getSelectionModel(), (i) -> {
            for (Song song : songTable.getItems()) {
                if (songColumns.get(i).getCellObservableValue(song).getValue() != null) {
                    return false;
                }
            }
            return true;
        });
        
        assignButtonAdd(addSongButton, () -> model.getSongs(), () -> new Song("new"));
        assignButtonDelete(deleteSongButton, () -> model.getSongs(), songTable.getSelectionModel(), (s) -> true);
        
        assignButtonAdd(addPlayerButton, () -> model.getPlayers(), () -> new Player("new", "", false));
        assignButtonDelete(deletePlayerButton, () -> model.getPlayers(), playerTable.getSelectionModel(), (p) -> {
            for (Song song : songTable.getItems()) {
                for (Player player : song.getPlayers()) {
                    if (player == p) {
                        return false;
                    }
                }
            }
            return true;
        });
        
        clipboardSongButton.setOnAction(this::clipboardSongButton_onAction);
        clipboardPlanningButton.setOnAction(this::clipboardPlanningButton_onAction);
        
        printSongButton.setOnAction(this::printSongButton_onAction);
        printPlanningButton.setOnAction(this::printPlanningButton_onAction);
        
        clearSongButton.setOnAction(this::clearSongButton_onAction);

        // drag and drop
        studioTable.setOnDragDetected(this::studio_onDragDetected);
        playerTable.setOnDragDetected(this::player_onDragDetected);
        songTable.setOnDragDetected(this::song_onDragDetected);
        
        songTable.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.ESCAPE) {
                if (songTable.getSelectionModel().getSelectedCells().size() <= 0) {
                    return;
                }
                int row = songTable.getSelectionModel().getSelectedCells().get(0).getRow();
                int col = songTable.getSelectionModel().getSelectedCells().get(0).getColumn();
                TableColumn column = songTable.getSelectionModel().getSelectedCells().get(0).getTableColumn();
                Instrument instrument = (Instrument) column.getUserData();
                if (col == 1) {
                    songTable.getItems().get(row).setPreferedStudio(null);
                } else {
                    songTable.getItems().get(row).removePlayer(instrument);
                }
                songTable.refresh();
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
                
                rightLabel.setText("Press [esc] to clear highlight. Hold [alt] and click for multi-selection.");
                
            }
            
        });
        
        playerTable.getSelectionModel().getSelectedCells().addListener((Observable observable) -> {
            clearHighlights();
            Player player = playerTable.getSelectionModel().getSelectedItem();
            planningTable.getItems().forEach((slot1) -> {
                slot1.applyPlayer(player);
            });
            planningTable.refresh();
            
            rightLabel.setText("Press [esc] to clear highlight.");
            
        });

        // load file
        String path = Configuration.getInstance().currentFile.get();
        File file = new File(path);
        if (!path.isEmpty() && file.exists()) {
            openFile(file);
        } else {
            loadModel();
        }
        
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
        
        rightLabel.setText("");
    }
    
    private <T> TableColumn<T, String> addEditableStringTableColumn(String columnName, TableView<T> tableView,
            Function<T, SimpleStringProperty> propertyAccessor,
            BiConsumer<T, String> propertySetter) {
        
        EventHandler<CellEditEvent<T, String>> editEvent = (CellEditEvent<T, String> event) -> {
            propertySetter.accept(((T) event.getTableView().getItems()
                    .get(event.getTablePosition().getRow())), event.getNewValue());
        };
        
        TableColumn<T, String> column = new TableColumn(columnName);
        column.setEditable(true);
        column.setCellValueFactory(cellData -> propertyAccessor.apply(cellData.getValue()));
        column.setCellFactory(EditCell.<T>forTableColumn());
        column.setOnEditCommit(editEvent);
        
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
    
    private <T, U> TableColumn<T, U> addEditableObjectTableColumn(String columnName, TableView<T> tableView, Supplier<ObservableList<U>> listSupplier,
            Function<T, SimpleObjectProperty<U>> propertyAccessor,
            BiConsumer<T, U> propertySetter) {
        
        TableColumn<T, U> column = new TableColumn(columnName);
        column.setEditable(true);
        column.setCellValueFactory(cellData -> propertyAccessor.apply(cellData.getValue()));
        column.setCellFactory((TableColumn<T, U> param) -> {
            ComboBoxTableCell cell = new ComboBoxTableCell(listSupplier.get());
            cell.setOnDragOver((event) -> {
                this.songCell_onDragOver(event, cell);
            });
            cell.setOnDragDropped((event) -> {
                this.songCell_onDragDropped(event, cell);
            });
            return cell;
        });
        
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
    
    private <T> void assignButtonDelete(Button button, Supplier<ObservableList<T>> getList, TableView.TableViewSelectionModel<T> selectionModel, Predicate<T> predicate) {
        button.setOnMouseClicked((event) -> {
            if (predicate.test(selectionModel.getSelectedItem())) {
                getList.get().remove(selectionModel.getSelectedItem());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Integrity violation");
                alert.setHeaderText(String.format("[%s] is already assigned !\nCannot be deleted !", selectionModel.getSelectedItem()));
                alert.showAndWait();
            }
        });
    }
    
    private void runPlanner() {
        planner = new SongPlanner(model);
        planner.compute();
        
        Planning planning = planner.next();
        if (planning != null) {
            List<Slot> slots = new ArrayList<>();
            
            int i = 0;
            Slot slot = null;
            for (Song song : planning.getPlanning()) {
                int s = i % model.getStudioDomainSize();
                int t = i / model.getStudioDomainSize();
                
                if (s == 0) {
                    slot = new Slot("Slot " + (t + 1));
                    slots.add(slot);
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
        
        leftLabel.setText(String.format("Solution found in [%d] steps !", planner.getStepCount()));
        
    }
    
    private void clearSongColumns() {
        if (songTable.getColumns().size() >= 3) {
            songTable.getColumns().remove(2);
        }
        for (Instrument instrument : songColumns.keySet()) {
            songTable.getColumns().remove(songColumns.get(instrument));
        }
        songColumns.clear();
    }
    
    private void createSongColumns() {
        addEditableObjectTableColumn("Prefered studio", songTable, () -> model.getStudios(), (t) -> t.getPreferedStudioProperty(), (t, v) -> t.setPreferedStudio(v));
        
        for (Instrument instrument : model.getInstruments()) {
            TableColumn col = addEditableObjectTableColumn(instrument.getName(), songTable, () -> model.getPlayers(), (t) -> t.getPlayerProperty(instrument), (t, v) -> t.addPlayer(instrument, v));
            instrument.getNameProperty().addListener((observable, oldValue, newValue) -> {
                col.setText(newValue);
            });
            col.setUserData(instrument);
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
        
        leftLabel.setText("New planning created.");
    }
    
    private void openFile(File file) {
        Strategy strategy = new CycleStrategy("_id", "_ref");
        Serializer serializer = new Persister(strategy);
        
        try {
            this.model = serializer.read(DataModel.class, file);
            loadModel();
            
            leftLabel.setText(String.format("File [%s] opened successfully !", file.getName()));
            
        } catch (Exception ex) {
            Logger.getLogger(PlannerControler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void saveFile(File file) {
        try {
            Strategy strategy = new CycleStrategy("_id", "_ref");
            Serializer serializer = new Persister(strategy);
            serializer.write(model, file);
            
            leftLabel.setText(String.format("File [%s] saved successfully !", file.getName()));
            
        } catch (Exception ex) {
            Logger.getLogger(PlannerControler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void openMenu_onAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Planner File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EMA studio planner file", FILE_CHOSER_EXTENSION));
        
        File directory = new File(Configuration.getInstance().currentPath.get());
        if (directory.exists()) {
            fileChooser.setInitialDirectory(directory);
        }
        
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            Configuration.getInstance().currentFile.set(file.getAbsolutePath());
            Configuration.getInstance().currentPath.set(file.getParent());
            
            openFile(file);
        }
    }
    
    protected void saveMenu_onAction(ActionEvent event) {
        File file = new File(Configuration.getInstance().currentFile.get());
        saveFile(file);
    }
    
    protected void saveAsMenu_onAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as Planner File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EMA studio planner file", FILE_CHOSER_EXTENSION));
        fileChooser.setInitialDirectory(new File(Configuration.getInstance().currentPath.get()));
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            if (!file.getAbsolutePath().endsWith(FILE_EXTENSION)) {
                file = new File(file.getAbsolutePath() + "." + FILE_EXTENSION);
            }
            
            Configuration.getInstance().currentFile.set(file.getAbsolutePath());
            Configuration.getInstance().currentPath.set(file.getParent());
            
            saveFile(file);
        }
        
    }
    
    protected void revertMenu_onAction(ActionEvent event) {
        openFile(new File(Configuration.getInstance().currentFile.get()));
    }
    
    protected void quitMenu_onAction(ActionEvent event) {
        Platform.exit();
    }
    
    protected void aboutMenu_onAction(ActionEvent event) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("About studio planner");
        dialog.setHeaderText("Studio planner\n© Yann Caron 2018\nUnder GPL License.");
        
        dialog.setGraphic(new ImageView(this.getClass().getResource("/icon-128.png").toString()));
        
        dialog.showAndWait();
    }
    
    protected void runButton_onClick(MouseEvent event) {
        runPlanner();
    }
    
    private void copyTableToClipboard(TableView table) {
        StringBuilder csv = new StringBuilder();
        int rows = table.getItems().size();
        int cols = table.getColumns().size();
        for (int r = 0; r < rows; r++) {
            if (r != 0) {
                csv.append("\n");
            }
            for (int c = 0; c < cols; c++) {
                if (c != 0) {
                    csv.append("\t");
                }
                TableColumn col = (TableColumn) table.getColumns().get(c);
                csv.append("\"");
                Object value = col.getCellObservableValue(r).getValue();
                if (value != null) {
                    csv.append(value);
                }
                csv.append("\"");
            }
        }
        
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(csv.toString());
        clipboard.setContent(content);
    }
    
    protected void clipboardSongButton_onAction(ActionEvent event) {
        copyTableToClipboard(songTable);
        leftLabel.setText("Song table copied as CSV in clipboard.");
        rightLabel.setText("Press [ctrl] + v to paste in Excel.");
    }
    
    protected void clipboardPlanningButton_onAction(ActionEvent event) {
        copyTableToClipboard(planningTable);
        leftLabel.setText("Planning table copied as CSV in clipboard.");
        rightLabel.setText("Press [ctrl] + v to paste in Excel.");
    }
    
    private void launchPrintPreview(String content, PageOrientation orientation) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/emacours/planner/PrintView.fxml"));
            
            Scene modalScene = new Scene(loader.load());
            Stage modalStage = new Stage();
            
            modalStage.setTitle("Print preview");
            modalStage.setScene(modalScene);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(stage);
            
            PrintControler controler = loader.getController();
            controler.setContent(content).setOrientation(orientation);
            controler.initializeEvents(modalStage, modalScene);
            
            modalStage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }
    
    protected void printSongButton_onAction(ActionEvent event) {
        HtmlTableGenerator<Song> htmlGenerator = new HtmlTableGenerator<>("Liste des morceaux", songTable.getItems());
        htmlGenerator.addColumn(new HtmlTableGenerator.Column<Song>("Nom", (t) -> t.getName()).setHeader(true));
        
        for (Instrument instrument : model.getInstruments()) {
            htmlGenerator.addColumn(new HtmlTableGenerator.Column<>(instrument.getName(), (t) -> "" + t.getPlayer(instrument)));
        }
        
        htmlGenerator.addColumn(new HtmlTableGenerator.Column<>("Remarques", (t) -> t.getComment()));
        
        String content = htmlGenerator.buildContent();
        launchPrintPreview(content, PageOrientation.LANDSCAPE);
    }
    
    protected void printPlanningButton_onAction(ActionEvent event) {
        HtmlTableGenerator<Slot> htmlGenerator = new HtmlTableGenerator<>("Répétition", planningTable.getItems());
        htmlGenerator.addColumn(new HtmlTableGenerator.Column<Slot>("Horaire", (t) -> t.getName()).setHeader(true));
        
        for (Studio studio : model.getStudios()) {
            htmlGenerator.addColumn(new HtmlTableGenerator.Column<>(studio.getName(), (t) -> "" + t.getSong(studio)));
        }
        
        String content = htmlGenerator.buildContent();
        launchPrintPreview(content, PageOrientation.PORTRAIT);
    }
    
    protected void clearSongButton_onAction(ActionEvent event) {
        model.getSongs().clear();
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
                TableColumn col = addEditableObjectTableColumn(instrument.getName(), songTable, () -> model.getPlayers(), (t) -> t.getPlayerProperty(instrument), (t, v) -> t.addPlayer(instrument, v));
                instrument.getNameProperty().addListener((observable, oldValue, newValue) -> {
                    col.setText(newValue);
                });
                songColumns.put(instrument, col);
            }
            
            for (Instrument instrument : change.getRemoved()) {
                TableColumn col = songColumns.get(instrument);
                songColumns.remove(instrument);
                songTable.getColumns().remove(col);
            }
        }
    }
    
    public static final String DRAG_AND_DROP_KEY_PLAYER = "PLAYER";
    public static final String DRAG_AND_DROP_KEY_STUDIO = "STUDIO";
    
    protected void studio_onDragDetected(MouseEvent event) {
        Dragboard db = playerTable.startDragAndDrop(TransferMode.COPY);
        ClipboardContent content = new ClipboardContent();
        
        content.putString(DRAG_AND_DROP_KEY_STUDIO);
        Studio studio = studioTable.getSelectionModel().getSelectedItem();
        dragAndDropStudio = studio;
        
        db.setContent(content);
        event.consume();
    }
    
    protected void player_onDragDetected(MouseEvent event) {
        Dragboard db = playerTable.startDragAndDrop(TransferMode.COPY);
        ClipboardContent content = new ClipboardContent();
        
        content.putString(DRAG_AND_DROP_KEY_PLAYER);
        Player player = playerTable.getSelectionModel().getSelectedItem();
        dragAndDropPlayer = player;
        
        db.setContent(content);
        event.consume();
    }
    
    protected void song_onDragDetected(MouseEvent event) {
        Dragboard db = playerTable.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        
        TableColumn column = songTable.getSelectionModel().getSelectedCells().get(0).getTableColumn();
        Song song = songTable.getSelectionModel().getSelectedItem();
        
        if (column.getUserData() instanceof Instrument) {
            content.putString(DRAG_AND_DROP_KEY_PLAYER);
            
            Instrument instrument = (Instrument) column.getUserData();
            Player player = song.getPlayerProperty(instrument).get();
            dragAndDropPlayer = player;
            song.removePlayer(instrument);
        } else {
            content.putString(DRAG_AND_DROP_KEY_STUDIO);
            
            Studio studio = song.getPreferedStudio();
            dragAndDropStudio = studio;
            song.setPreferedStudio(null);
        }
        
        db.setContent(content);
        event.consume();
    }
    
    private Player dragAndDropPlayer;
    private Studio dragAndDropStudio;
    
    protected void songCell_onDragOver(DragEvent event, ComboBoxTableCell cell) {
        Dragboard db = event.getDragboard();
        if (db.hasString() && db.getString().equals(DRAG_AND_DROP_KEY_PLAYER)
                || db.getString().equals(DRAG_AND_DROP_KEY_STUDIO)) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }
    
    protected void songCell_onDragDropped(DragEvent event, ComboBoxTableCell cell) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            int row = cell.getTableRow().getIndex();
            if (row < songTable.getItems().size()) {
                Song song = songTable.getItems().get(row);
                
                if (event.getTransferMode() == TransferMode.COPY) {
                    if (db.getString().equals(DRAG_AND_DROP_KEY_PLAYER) && dragAndDropPlayer != null) {
                        
                        Instrument instrument = (Instrument) cell.getTableColumn().getUserData();
                        
                        song.addPlayer(instrument, dragAndDropPlayer);
                        success = true;
                    } else if (db.getString().equals(DRAG_AND_DROP_KEY_STUDIO) && dragAndDropStudio != null) {
                        song.setPreferedStudio(dragAndDropStudio);
                        success = true;
                    }
                } else {
                    if (db.getString().equals(DRAG_AND_DROP_KEY_PLAYER) && dragAndDropPlayer != null) {
                        Instrument instrument = (Instrument) cell.getTableColumn().getUserData();
                        song.addPlayer(instrument, dragAndDropPlayer);
                        success = true;
                    } else if (db.getString().equals(DRAG_AND_DROP_KEY_STUDIO) && dragAndDropStudio != null) {
                        song.setPreferedStudio(dragAndDropStudio);
                        success = true;
                    }
                }
            }
            songTable.refresh();
        }
        event.setDropCompleted(success);
        event.consume();
    }
    
}
