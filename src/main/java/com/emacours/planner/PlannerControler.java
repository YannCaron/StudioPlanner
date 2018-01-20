/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

import com.emacours.planner.model.DataModel;
import com.emacours.planner.model.Studio;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

/**
 *
 * @author cyann
 */
public class PlannerControler implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(PlannerControler.class.getName());
    DataModel model;

    @FXML
    public TableView<Studio> studioTable;

    @FXML
    public Spinner slotSpinner;

    @FXML
    public Spinner durationSpinner;

    @FXML
    public Button runButton;

    @FXML
    private Accordion accord;

    @FXML
    private TitledPane studioPane, studentPane;

    public PlannerControler() {
        model = new DataModel();
        model.getStudios().add(new Studio("Batterie"));
        model.getStudios().add(new Studio("Studio B"));
        model.getStudios().add(new Studio("Studio C"));
    }

    //Define the button cell
    private class ButtonCell extends TableCell<Studio, Boolean> {

        final Button cellButton = new Button("-");

        ButtonCell() {

            cellButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    // do something when button clicked
                    //...
                    final Studio item = (Studio) getTableRow().getItem();
                    model.getStudios().remove(item);
                    System.out.println(item.getName());
                    getTableView().refresh();
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(cellButton);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        accord.setExpandedPane(studioPane);

        slotSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 5));
        slotSpinner.getValueFactory().valueProperty().bindBidirectional(model.getMaxSlotProperty());

        ///durationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 240, 45, 15));

        runButton.setOnMouseClicked((event) -> {
            System.out.println(model.toXML());
        });

        studioTable.setEditable(true);
        Callback<TableColumn<Studio, String>, TableCell<Studio, String>> cellFactory
                = (TableColumn<Studio, String> param) -> new TextFieldTableCell<>();

        TableColumn<Studio, String> nameCol = new TableColumn("Name");
        nameCol.setMinWidth(100);
        nameCol.setEditable(true);
        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        nameCol.setCellFactory(TextFieldTableCell.<Studio>forTableColumn());
        nameCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Studio, String> t) -> {
                    ((Studio) t.getTableView().getItems()
                            .get(t.getTablePosition().getRow()))
                            .setName(t.getNewValue());

                });

        TableColumn<Studio, Boolean> delColumn = new TableColumn<>("Del");

        delColumn.setCellValueFactory((TableColumn.CellDataFeatures<Studio, Boolean> p)
                -> new SimpleBooleanProperty(p.getValue() != null));

        delColumn.setCellFactory((TableColumn<Studio, Boolean> p) -> new ButtonCell());

        studioTable.setItems(model.getStudios());
        studioTable.getColumns().addAll(nameCol, delColumn);
    }

    public void setData() {
    }

}
