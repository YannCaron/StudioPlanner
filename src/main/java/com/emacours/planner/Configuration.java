/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author cyann
 */
public class Configuration {

    public class Prop<T> {

        private final String name;
        private T value;
        private final T defaultValue;

        public Prop(String name, T defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public Prop(String name) {
            this(name, null);
        }

        public final T get() {
            if (value == null) {

                if (defaultValue != null) {
                    value = (T) prop.getProperty(name, (String) defaultValue);
                } else {
                    value = (T) prop.getProperty(name);
                }
            }

            return value;
        }

        public final void set(T v) {
            if (value == null || !value.equals(v)) {
                value = v;
                prop.setProperty(name, (String) v);
                save();
            }
        }

        @Override
        public String toString() {
            return "Prop{" + "name=" + name + ", cache=" + value + ", defaultValue=" + defaultValue + '}';
        }

    }

    // constants
    public static String PROPERTY_PATH = "config.properties";
    private static final Logger LOGGER = Logger.getLogger(Configuration.class.toString());

    // internal attributes
    private static Configuration singleton;
    private final Properties prop = new Properties();

    // property beans
    public final Prop<String> currentFile = new Prop<>("current-file", "");
    public final Prop<String> currentPath = new Prop<>("current-path", "");

    // load / save
    private void load() {
        InputStream input = null;

        if (!Files.exists(Paths.get(PROPERTY_PATH))) {
            return;
        }

        try {

            input = new FileInputStream(PROPERTY_PATH);

            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
            LOGGER.severe(String.format("Cannot load [%s] property file !", PROPERTY_PATH));
            LOGGER.throwing(this.getClass().toString(), "load", ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    LOGGER.severe(String.format("Cannot close [%s] property file !", PROPERTY_PATH));
                    LOGGER.throwing(this.getClass().toString(), "load", ex);
                }
            }
        }

    }

    private void save() {
        OutputStream output = null;

        try {

            output = new FileOutputStream(PROPERTY_PATH);

            // save properties to project root folder
            prop.store(output, "property file");
            output.flush();

        } catch (IOException ex) {
            LOGGER.severe(String.format("Cannot save [%s] property file !", PROPERTY_PATH));
            LOGGER.throwing(this.getClass().toString(), "save", ex);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    LOGGER.severe(String.format("Cannot close [%s] property file !", PROPERTY_PATH));
                    LOGGER.throwing(this.getClass().toString(), "save", ex);
                }
            }

        }
    }

    // constructor
    private Configuration() {
        load();
    }

    public static Configuration getInstance() {
        if (singleton == null) {
            singleton = new Configuration();
        }
        return singleton;
    }

}