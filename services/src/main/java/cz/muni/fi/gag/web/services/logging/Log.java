package cz.muni.fi.gag.web.services.logging;

import org.jboss.logging.Logger;

import java.util.Arrays;

/**
 * @author Vojtech Prusa
 * <p>
 * TODO add logging by service and/or service type
 * - RS
 * - WS
 * -- RECORD|REPLAY|RECOGNIZE
 * <p>
 * Also do it in performance firendy manner... do not instantiate anything unnecessary depending on log severity
 */
public final class Log {

    // TODO load from env variable, config file, option -D
    public static Class[] enabledLogTypes = {
//            LoggerTypeWSRecorder.class,
//            LoggerTypeWSReplayer.class,
            LoggerTypeWSRecognizer.class,
            LoggerTypeWSRecognizerComparator.class
    };

    public static class LoggerType {}

    public static class LoggerTypeWSRecorder extends LoggerType {}

    public static class LoggerTypeWSReplayer extends LoggerType {}

    public static class LoggerTypeWSRecognizer extends LoggerType {}

    public static class LoggerTypeWSRecognizerComparator extends LoggerType {}

    public static class LoggerTypeDLDecoder extends LoggerType {}

    public static final Logger logger = Logger.getLogger(Log.class.getSimpleName());

    public static void info(Object s) {
        logger.info(s);
    }

    public static void info(Class c, Object s) {
        if (s instanceof String) {
            logger.info(c.getSimpleName() + ": " + s.toString());
        } else {
            logger.info(c.getSimpleName() + ": " + s.toString());
        }
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

    public static void infoDelete(Class c, Object s) {
        info(c, " deleted: " + s.toString());
    }

    /**
     * It may not be the best solution, but this should result in multiple classes so the goal to have static logger is
     * done and the Sheep is not eaten because Wolf (~ Logger ) is static across whole app.. ?
     * Or is there an issue with the resource lock for logger and this whole idea will not work for multiple
     * simultaneous loggers (multiple users or apps running)?
     * It is not the best way but idk and I am lazy to find out how to config wildfly logging for 2D verbosity/severity.
     * 2D because 1D is info,warn,err,trace and 2. dimension is the app
     * and not adding flags to wrapper info(typeFlag, msg)
     **/
    public static class TypedLogger<T extends LoggerType> {
        public final Class t;
        public static final Logger logger = Logger.getLogger(Log.class.getSimpleName());

        public TypedLogger(Class t) {
            this.t = t;
        }

        public void info(Object o) {
            if (Arrays.asList(Log.enabledLogTypes).contains(t)){
                Log.info(o);
            }
        }

    }
}
