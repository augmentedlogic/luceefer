/**
 * (c) 2026 Wolfgang Hauptfleisch <dev@augmentedlogic.com>
 * This file is part of luceefer
 * Licence: Apache v2
 **/
package com.augmentedlogic.luceefer;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Handles loading the optional config file
 * currently only for testing
 **/
public class ConfigLoader {

    /**
     * loads the config file (optional) and sets properties
     *
     * @param args command line arguments
     **/
    public static void load(String[] args) {
        String configfile = null;
        for (int i = 0; i < args.length; i++) {
            if(args[i].equals("--config")) {
                if(args.length > i+1) {
                    configfile = args[i+1];
                    break;
                }
            }
        }

        if(configfile != null) {

            Properties properties = new Properties();

            try (FileInputStream input = new FileInputStream(configfile)) {
                properties.load(input);
            } catch (IOException e) {
                System.out.println("Specified config file not found. Reverting to default settings.");
            }

        }

    }
}
