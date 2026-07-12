/**
 * (c) 2026 Wolfgang Hauptfleisch <dev@augmentedlogic.com>
 * This file is part of luceefer
 * Licence: Apache v2
 **/
package com.augmentedlogic.luceefer;

import java.util.*;
import java.io.File.*;
import com.augmentedlogic.flere.service.*;

/**
 * main entry point that starts the service
 **/
class Main {
    public static void main( String[] args ) {

        // TODO: load option config file


        System.out.println("Listening on localhost:9666\n");

        FlereService ns = new FlereService("localhost", 9666);
        ns.addHandler("/api", new ApiHandler());
        ns.setBacklog(8096);
        ns.setDebug(false);
        try {
            ns.start();
        } catch(Exception e) {
            System.out.println(e);
        }

    }

}
