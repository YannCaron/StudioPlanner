/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrintColor;
import javafx.print.PrintQuality;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 *
 * @author cyann
 */
public class PrintControler implements Constants, Initializable {

    private Stage stage;
    private Scene scene;
    private PageOrientation orientation;

    @FXML
    WebView webView;

    @FXML
    Button printButton;

    public PageOrientation getOrientation() {
        return orientation;
    }

    public PrintControler setOrientation(PageOrientation orientation) {
        this.orientation = orientation;
        return this;
    }

    public PrintControler setContent(String content) {
        webView.getEngine().loadContent(content);
        return this;
    }

    public PrintControler() {
        this.orientation = PageOrientation.PORTRAIT;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void initializeEvents(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;

        printButton.setOnAction(this::printButton_onClick);
    }

    protected void printButton_onClick(ActionEvent e) {
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, orientation, Printer.MarginType.DEFAULT);

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            job.getJobSettings().setPrintColor(PrintColor.COLOR);
            job.getJobSettings().setPrintQuality(PrintQuality.HIGH);
            job.getJobSettings().setPageLayout(pageLayout);
            if (job.showPrintDialog(stage)) {
                webView.getEngine().print(job);
            }
            job.endJob();
        }
    }
}
