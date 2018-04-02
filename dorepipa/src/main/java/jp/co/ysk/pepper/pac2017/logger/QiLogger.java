package jp.co.ysk.pepper.pac2017.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QiLogger {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(QiLogger.class);

    public static void error(String text) {
        logger.error(text);
    }

    public static void warn(String text) {
        logger.warn(text);
    }

    public static void info(String text) {
        logger.info(text);
    }

    public static void debug(String text) {
        logger.debug(text);
    }

    public static void trace(String text) {
        logger.trace(text);
    }
}
