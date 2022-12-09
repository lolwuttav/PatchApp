package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    public static void log(String message) {
        System.out.println(getDate() + message);
    }
    public static String getDate() {


       Date date = new Date();
       SimpleDateFormat formatter = new SimpleDateFormat("[HH:mm:ss] ");
         String strDate = formatter.format(date);
         return strDate;
    }



    // Prepends the current time in the format HH:MM:SS to the input string
    public static String logTime(String input) {
        // Get the current time
        Date now = new Date();

        // Format the time as a string
        String timeString = timeFormat.format(now);

        // Prepend the time string to the input string
        return timeString + ": " + input;
    }

}

