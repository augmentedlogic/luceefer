/**
 * (c) 2026 Wolfgang Hauptfleisch <dev@augmentedlogic.com>
 * This file is part of luceefer
 * Licence: Apache v2
 **/
package com.augmentedlogic.luceefer;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * the response object that will be encoded as json
 */
public class Response {

    private HashMap<String, Object> response = new HashMap<String, Object>();
    private String uuid = null;

    /**
     * set the api status code
     *
     * @param status the status(int) to be returned
     */
    protected void setStatus(int status) {
        this.response.put("status", status);
    }

    /**
     * set the method
     *
     * @param method set the method that was called in ApiHandler
     */
    protected void setMethod(String method) {
        this.response.put("method", method);
    }

    /**
     * set a custom field
     *
     * @param key key name
     * @param value value for that key
     */
    protected void setCustomField(String key, Object value) {
        this.response.put(key, value);
    }

    /**
     * add the results to the returned json
     *
     * @param searchResults the arraylist with the results
     */
    protected void setResults(ArrayList searchResults) {
        this.response.put("results", searchResults);
        this.response.put("items", searchResults.size());
    }

    /**
     * return object as json
     *
     * @return the json string
     */
    protected String render() {

        String json = "[]";

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            json = gson.toJson(this.response);
        } catch(Exception e) {

        }

        return json;
    }

}
