package com.emacours.planner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author cyann
 */
public class FXMain extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/emacours/planner/PlannerView.fxml"));
        Parent root = loader.load();
        PlannerControler controler = loader.getController();

        primaryStage.setTitle("Studio Planner");
        /*primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon-16.png")));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon-32.png")));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon-64.png")));*/
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon-128.png")));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
