package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

    private static final String TIME_PATTERN = "[HH:mm:ss] ";

    public static void log(String message) {
        System.out.println(getDate() + message);
    }

    public static String getDate() {
        Date now = new Date();
        return new SimpleDateFormat(TIME_PATTERN).format(now);
    }
}
