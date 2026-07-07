/**
 * (c) 2026 Wolfgang Hauptfleisch <dev@augmentedlogic.com>
 * This file is part of luceefer
 * Licence: Apache v2
 **/
package com.augmentedlogic.luceefer;

/**
 * payload object used by GSON
 **/
public class Payload {

    String uuid;
    String body;

    String title;
    String link;

    /**
     * @return the body of the payload
     **/
    public String getBody() {
        return this.body;
    }

    /**
     * @return the id of the payload
     **/
    public String getId() {
        return this.uuid;
    }

}
