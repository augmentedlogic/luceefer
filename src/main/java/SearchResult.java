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
    private String uuid = null;

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
    protected void setUuid(String uuid) {
        this.uuid = uuid;
    }

}

