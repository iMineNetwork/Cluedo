package nl.imine.minigame.cluedo.util;

import nl.imine.minigame.cluedo.CluedoPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private static Logger logger = CluedoPlugin.getInstance().getLogger();

    public static void log(Level level, String msg) {
        logger.log(level, msg);
    }

    public static void finest(String msg) {
        log(Level.FINEST, msg);
    }

    public static void finer(String msg) {
        log(Level.FINER, msg);
    }

    public static void fine(String msg) {
        log(Level.FINE, msg);
    }

    public static void info(String msg) {
        log(Level.INFO, msg);
    }

    public static void warning(String msg) {
        log(Level.WARNING, msg);
    }

    public static void severe(String msg) {
        log(Level.SEVERE, msg);
    }

    public static Logger getLogger() {
        return logger;
    }
}
