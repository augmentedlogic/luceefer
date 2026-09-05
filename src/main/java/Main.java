/**
 * (c) 2026 Wolfgang Hauptfleisch <dev@augmentedlogic.com>
 * This file is part of luceefer
 * Licence: Apache v2
 **/
package com.augmentedlogic.luceefer;

import java.util.*;
import java.io.File.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.augmentedlogic.flere.service.*;

/**
 * main entry point that starts the service
 **/
class Main {

    private static void loadConfig(String[] args) {
        // we are setting the defaults first
        System.setProperty("luceefer.port", "9666");
        System.setProperty("luceefer.bind", "localhost");
        System.setProperty("luceefer.indexdir", "/tmp/index.luceefer");
        System.setProperty("luceefer.analyzer", "simple");
        System.setProperty("luceefer.logfile", "none");
        System.setProperty("luceefer.debug", "0");

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
            System.out.println("Loading config from " + configfile);

            try (FileInputStream input = new FileInputStream(configfile)) {
                  Properties properties = new Properties();
                  properties.load(input);
                  properties.forEach((key, value) ->
                          //System.out.println("Key : " + key + ", Value : " + value)
                          System.setProperty((String) key, (String) value)
                          );
            } catch (IOException e) {
                System.out.println("Specified config file not found. Reverting to default settings.");
            }
        }

    }

    public static void main( String[] args ) {

        Main.loadConfig(args);

        System.out.println("Listening on " + System.getProperty("luceefer.bind") + ":" + System.getProperty("luceefer.port") + "\n");

        FlereService fs = new FlereService("localhost", 9666);
        fs.addHandler("/api", new ApiHandler());
        fs.setBacklog(8096);
        fs.setDebug(false);
        try {
            fs.start();
        } catch(Exception e) {
            System.out.println(e);
        }

    }

}
