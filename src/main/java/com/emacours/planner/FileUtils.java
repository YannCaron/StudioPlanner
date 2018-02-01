/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 *
 * @author cyann
 */
public class FileUtils {

    private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getName());

    private FileUtils() {
        throw new RuntimeException("Cannot instanciate static class !");
    }

    public static String loadRessource(String fileName) {

        StringBuilder result = new StringBuilder("");

        Scanner scanner = new Scanner(FileUtils.class.getResourceAsStream(fileName));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            result.append(line).append("\n");
        }

        scanner.close();

        return result.toString();

    }

}
