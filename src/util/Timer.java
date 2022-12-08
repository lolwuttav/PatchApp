package util;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Timer {
    // Format used to convert the elapsed time to a string
    private static DecimalFormat timeFormat = new DecimalFormat("#0.00");

    // The time that the timer was started
    private long startTime;

    // The message that was passed to the start method
    private String startMessage;

    private boolean printTime = true;

    // Starts the timer
    public void start(String message) {
        // Save the current time and the start message
        startTime = System.currentTimeMillis();
        startMessage = message;
    }
    public void start(Boolean printTime) {
        // Save the current time and the start message
        startTime = System.currentTimeMillis();
        this.printTime = printTime;
    }

    // Stops the timer and logs the elapsed time to the console
    public int stop() {
        // Calculate the elapsed time
        long elapsedTime = System.currentTimeMillis() - startTime;

        // Convert the elapsed time to seconds
        double elapsedTimeInSeconds = elapsedTime / 1000.0;

        // Format the elapsed time as a string
        String elapsedTimeString = timeFormat.format(elapsedTimeInSeconds);
        if(printTime) {
            // Log the elapsed time
            LogUtil.log(startMessage + ": " + elapsedTimeString + " seconds");
        }
        return (int) elapsedTime;
    }
}