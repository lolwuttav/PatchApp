package util;

import java.text.DecimalFormat;

public class Timer {
    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#0.00");

    private long startTime;
    private String startMessage;

    public void start(String message) {
        startTime = System.currentTimeMillis();
        startMessage = message;
    }

    public int stop() {
        long elapsedTimeMs = System.currentTimeMillis() - startTime;
        String elapsedTimeString = TIME_FORMAT.format(elapsedTimeMs / 1000.0);
        LogUtil.log(startMessage + ": " + elapsedTimeString + " seconds");
        return (int) elapsedTimeMs;
    }
}
