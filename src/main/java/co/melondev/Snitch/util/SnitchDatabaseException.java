package co.melondev.Snitch.util;

/**
 * Created by Devon on 8/6/18.
 */
public class SnitchDatabaseException extends Exception {

    public SnitchDatabaseException() {
    }

    public SnitchDatabaseException(String message) {
        super(message);
    }

    public SnitchDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SnitchDatabaseException(Throwable cause) {
        super(cause);
    }

    public SnitchDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
