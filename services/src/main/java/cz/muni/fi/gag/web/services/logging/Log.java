package cz.muni.fi.gag.web.services.logging;

import org.jboss.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 */
public class Log {

    public static Logger logger = Logger.getLogger(Log.class.getSimpleName());

    public static void info(String s) {
        logger.info(s);
    }

    public static void info(Class c, String s) {
        logger.info(c.getSimpleName() + ": " + s);
    }

    public static void infoCreate(Class c, Object s) {
        info(c, " created: " + s.toString());
    }

    public static void infoRemove(Class c, Object s) {
        info(c, " removed: " + s.toString());
    }

    public static void infoUpdate(Class c, Object s) {
        info(c, " updated: " + s.toString());
    }

}
