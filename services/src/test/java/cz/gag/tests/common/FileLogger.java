package cz.gag.tests.common;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Note:<br>
 * You know those times when it is faster to implement it on your own instead google solution across N versions
 *
 * @author vprusa
 */
public class FileLogger {

    public static final Logger getLogger(final String name){
        Logger logger = Logger.getLogger(name);
        FileHandler fh;
        try {
            final String logDir = System.getProperty("user.dir");
            final String logFilePath = new StringBuilder(logDir).append("/").append(name).append(".log").toString();
            // This block configure the logger with handler and formatter
            fh = new FileHandler(logFilePath);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }



}
