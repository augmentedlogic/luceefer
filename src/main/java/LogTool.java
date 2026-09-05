/**
 * (c) 2026 Wolfgang Hauptfleisch <dev@augmentedlogic.com>
 * This file is part of luceefer
 * Licence: Apache v2
 **/
package com.augmentedlogic.luceefer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.util.Properties;

/**
 * writes the access, debug and error log
 **/
public class LogTool {


    /**
     * default date format used
     **/
    private static final String DEFAULT_DATE_FORMAT = "[dd/MMM/yyyy:HH:mm:ss Z]";
    /**
     * access log file path
     **/
    private String logfile = null;

    /**
     * debug enabled
     **/
    private Boolean debug = false;

    /**
     * writes the access, debug and error log
     **/
    public LogTool() {

    }

    /**
     * get the point at which the log entry was written
     * currently not used
     *
     * @return returns the point where the error ocurred
     **/
    public static String getLogPoint() {
        return Thread.currentThread().getStackTrace()[2].getClassName() + File.separator + Thread.currentThread().getStackTrace()[2].getMethodName();
    }


    /**
     * writes a new line to the log
     *
     * @param target_logfile path to the log file to write to
     * @param msg the message added to the log file
     *
     **/
    private void writeTo(String target_logfile, String msg)  {

        Properties systemProperties = System.getProperties();
        String date_format = systemProperties.getProperty("luceefer.log.dateformat");
        if(date_format == null) {
            date_format = LogTool.DEFAULT_DATE_FORMAT;
        }

        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(date_format);
        String DateToStr = format.format(curDate);
        String logmsg = DateToStr + " " + msg + "\n";


        PrintWriter printWriter = null;
        File file = new File(target_logfile);

        try {
            if (!file.exists()) file.createNewFile();
            printWriter = new PrintWriter(new FileOutputStream(target_logfile, true));
            printWriter.write(logmsg);
        } catch (IOException e) {
            new LogTool().error(LogTool.getLogPoint(), e);
        } finally {
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
        }


    }


    /**
     * wrapper for writing to the access log
     *
     * @param msg the message added to the log file
     **/
    protected void write(String msg) {
        Properties systemProperties = System.getProperties();
        String access_log = systemProperties.getProperty("luceefer.logfile");
        if(access_log != null) {
            this.writeTo(access_log, msg);
        }
    }


    protected void debug(String msg) {
        Properties systemProperties = System.getProperties();
        String access_log = systemProperties.getProperty("luceefer.logfile");
        if(access_log != null) {
            this.writeTo(access_log, msg);
        }
    }


    /**
     * wrapper for writing to the error log
     *
     * @param logpoint the method where the error occurred
     * @param emsg the message added to the log file
     **/
    public void error(String logpoint, Exception emsg)  {
        Properties systemProperties = System.getProperties();
        String error_log = systemProperties.getProperty("luceefer.errorlog");
        if(error_log != null) {
            this.writeTo(error_log, logpoint + " : " + emsg.toString());
        } else {
            System.out.println(logpoint + " : " + emsg);
        }
    }

}

