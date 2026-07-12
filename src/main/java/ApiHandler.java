/**
 * (c) 2026 Wolfgang Hauptfleisch <dev@augmentedlogic.com>
 * This file is part of luceefer
 * Licence: Apache v2
 **/
package com.augmentedlogic.luceefer;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.nio.file.Paths;
import java.io.File.*;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.augmentedlogic.flere.service.*;

/**
 * ApiHandler implements the StandardHandler from flere-service
 */
public class ApiHandler implements StandardHandler {

    //protected Directory index = new ByteBuffersDirectory();
    //protected StandardAnalyzer analyzer = new StandardAnalyzer();
    //protected Ngram35Analyzer analyzer = new Ngram35Analyzer();

    /**
     * the index directory object
     */
    protected FSDirectory index = null;

    /**
     * the analyzer to be used
     */
    protected SimpleAnalyzer analyzer = new SimpleAnalyzer();


    /**
     * delete document from index so it can be set again
     *
     * @param uuid the document id
     */
    private Boolean deleteOnDemand(String uuid) {

        Boolean existed = false;
        IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
        IndexWriter w = null;

        try {
            w = new IndexWriter(this.index, config);

            IndexReader reader = DirectoryReader.open(this.index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs results = searcher.search(new TermQuery(new Term("uuid", uuid)), 1);

            int totalHits = (int) results.totalHits.value ;
            if (totalHits > 0) {
                existed = true;
                w.deleteDocuments(new Term("uuid", uuid));
            }

        } catch(Exception e) {


        } finally {

            if(w != null) {
                try {
                    w.close();
                } catch (Exception ex) {
                    new LogTool().error(LogTool.getLogPoint(), ex);
                }
            }


        }

        return existed;
    }


    private void addDoc(IndexWriter w, String uuid, String content) throws Exception {
        Document doc = new Document();
        doc.add(new StringField("uuid", uuid, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));
        w.addDocument(doc);
    }

    /**
     * handler for /api
     *
     **/
    public HttpResponse handle(HttpRequest request) {

        Response res = new Response();
        long start_time = System.currentTimeMillis();

        try {
            String idirectory = System.getProperty("java.io.tmpdir") + java.io.File.separator + "index.luceefer";
            this.index = FSDirectory.open(Paths.get(idirectory));
        } catch(Exception e) {
            new LogTool().debug("handle: " + e);
        }

        String m = request.getString("m", "invalid");
        String payload = request.getPostdata();

        Integer o = request.getInteger("o", 0);
        String q = request.getString("q");
        String uuid = null;
        String body = null;

        new LogTool().debug("METHOD: " + m);
        res.setMethod(m);

        switch(m) {

        case "add":

            Payload p = null;
            Boolean updated = false;

            try {
                Gson gson = new Gson();
                p = gson.fromJson(payload, Payload.class);
                new LogTool().debug("FOUND: " + p.getId());
                new LogTool().debug("FOUND: " + p.getBody());
                uuid = p.getId();
                body = p.getBody();

            } catch(Exception e) {
                new LogTool().error(LogTool.getLogPoint(), e);
                res.setStatus(500);

            }

            try {
                updated = deleteOnDemand(uuid);
            } catch(Exception e) {
                new LogTool().error(LogTool.getLogPoint(), e);
                res.setStatus(500);

            }

            try {

                IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
                IndexWriter w = new IndexWriter(this.index, config);
                addDoc(w, uuid, body);
                w.close();
                if(updated == true) {
                    res.setStatus(201);
                } else {
                    res.setStatus(200);
                }
            } catch(Exception e) {
                new LogTool().error(LogTool.getLogPoint(), e);
                res.setStatus(500);
            }


            break;

        case "reset":

            try {
                IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
                IndexWriter w = new IndexWriter(this.index, config);
                w.deleteAll();
                w.close();
                res.setStatus(200);

            } catch(Exception e) {
                new LogTool().error(LogTool.getLogPoint(), e);
                res.setStatus(500);

            }

            break;


        case "query":

            try {

                int hitsPerPage = 20;

                QueryParser qp = new QueryParser("content", this.analyzer);
                if(o == 0) {
                    qp.setDefaultOperator(QueryParser.Operator.AND);
                } else if(o == 1) {
                    qp.setDefaultOperator(QueryParser.Operator.OR);
                }
                Query query = qp.parse(q);

                IndexReader reader = DirectoryReader.open(this.index);
                IndexSearcher searcher = new IndexSearcher(reader);
                TopDocs docs = searcher.search(query, hitsPerPage);
                ScoreDoc[] hits = docs.scoreDocs;

                ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();

                for(int i=0; i<hits.length; ++i) {
                    int docId = hits[i].doc;
                    double docScore = hits[i].score;
                    Document d = searcher.doc(docId);
                    SearchResult searchResult = new SearchResult();
                    searchResult.setId((String) d.get("uuid"));
                    searchResult.setScore(docScore);
                    //searchResult.setBody((String) d.get("content"));
                    searchResults.add(searchResult);
                }


                res.setStatus(200);
                res.setResults(searchResults);
                res.setCustomField("took", System.currentTimeMillis() - start_time);

            } catch(Exception e) {
                new LogTool().error(LogTool.getLogPoint(), e);
                res.setStatus(500);
            }

            break;

        case "status":

            try {
                IndexReader read = DirectoryReader.open(this.index);
                int num = read.numDocs();
                res.setStatus(200);
                res.setCustomField("documents", num);
                long mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                res.setCustomField("memory", mem);
                // directory size

            } catch(Exception e) {
                new LogTool().error(LogTool.getLogPoint(), e);
                res.setStatus(500);
            }


            break;

        default:


            break;
        }

        HttpResponse hr = new HttpResponse();
        hr.setHttpStatus(200);
        hr.setContentType("application/json; charset=utf-8");
        hr.setBody(res.render());
        return hr;

    }

}



