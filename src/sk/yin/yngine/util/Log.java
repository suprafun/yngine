package sk.yin.yngine.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * Basic logging interface for Yngine code.
 * @author yin
 */
public class Log {
    private static Log instance;

    private Log() {
    }

    /**
     * Returns a Singleton.
     * @return Instance.
     */
    public static Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }

    // TODO(mgagyi): What about log(Object[] objs...) syntax?
    /**
     * Forwards to instance msg() method.
     * @param message
     * @param exception
     */
    public static void log(String message) {
        getInstance().msg(message);
    }

    /**
     * Forwards to instance msg() method.
     * @param message
     * @param exception
     */
    public static void log(String message, Exception exception) {
        getInstance().msg(message, exception);
    }

    /**
     * Logs message.
     * @param message
     * @param exception
     */
    public void msg(String message) {
        String time = getTime();
        System.out.println(time + ": " + message);
    }

    /**
     * Logs message and parts of the exception.
     * @param message
     * @param exception
     */
    public void msg(String message, Exception exception) {
        String time = getTime();
        System.err.println(time + ": " + message + ": " + exception.getClass().getName() + ": " + exception.getMessage());
    }

    protected String getTime() {
        long time = System.currentTimeMillis();
        return DateFormatUtils.format(time, "hh:mm:ss.SSS");
    }
}
