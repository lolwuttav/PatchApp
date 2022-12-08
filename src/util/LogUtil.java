package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    public static void log(String message) {
        System.out.println(getDate() + message);
    }
    public static String getDate() {


       Date date = new Date();
       SimpleDateFormat formatter = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss] ");
         String strDate = formatter.format(date);
         return strDate;
    }

}

