/**
 * (c) 2026 Wolfgang Hauptfleisch <dev@augmentedlogic.com>
 * This file is part of luceefer
 * Licence: Apache v2
 **/
package com.augmentedlogic.luceefer;

import java.util.*;

/**
 * search result object
 */
public class SearchResult {

    private String body = null;
    private String id = null;
    private double score = 0.0;

    /**
     * set the body
     *
     * @param body the indexed content, only if debugging is on
     */
    protected void setBody(String body) {
        this.body = body;
    }

    /**
     * set the id
     *
     * @param uuid the id of the document found
     */
    protected void setId(String uuid) {
        this.id = uuid;
    }

    /**
     * set the score
     *
     * @param score of the result
     */
    protected void setScore(double score) {
        this.score = score;
    }

}

