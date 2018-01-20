/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

import com.emacours.planner.algorithm.SongPlanner;
import com.emacours.planner.model.DataModel;

/**
 *
 * @author formation
 */
public class Main {

    public static void main(String[] args) {
        DataModel model = DataModel.buildDummy();
        SongPlanner planner = new SongPlanner(model);
        planner.compute();

        while (planner.hasNext()) {
            planner.next();
        }

    }

}
