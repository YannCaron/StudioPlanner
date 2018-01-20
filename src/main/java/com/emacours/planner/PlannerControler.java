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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private Accordion accord;
    
    @FXML
    private TitledPane studioPane, studentPane;

    public PlannerControler() {
        model = new DataModel();
        model.getStudios().add(new Studio("Batterie"));
        model.getStudios().add(new Studio("Studio B"));
        model.getStudios().add(new Studio("Studio C"));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        accord.setExpandedPane(studioPane);
        
        studioTable.setEditable(true);

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setMinWidth(100);
        nameCol.setEditable(true);
        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        studioTable.setItems(model.getStudios());
        studioTable.getColumns().addAll(nameCol);
    }

    public void setData() {
    }

}
