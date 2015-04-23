package com.jonatantierno.trellotimer;

import android.content.Context;

import com.jonatantierno.trellotimer.model.Task;

/**
 * Utility class to pretty print times.
 * Created by jonatan on 23/04/15.
 */
public final class Formatter {
    public static final int MS_PER_H = 3600 * 1000;
    public static final int MS_PER_MIN = (60 * 1000);

    // Private constructor to prevent instantiation
    private Formatter(){}

    /**
     * Formats an amount of time as in: "5h 13m 24s"
     * @param timeSpent time in milliseconds
     * @return pretty string
     */
    public static String formatTimeSpent(long timeSpent) {
        return formatTimeSpent(timeSpent, new StringBuilder()).toString();
    }

    /**
     * Formats an amount of time as in: "5h 13m 24s"
     * @param timeSpent time in milliseconds
     * @param b builder to use in the construction.
     * @return same builder for fluent interface
     */
    public static StringBuilder formatTimeSpent(long timeSpent,StringBuilder b) {
        long hours= timeSpent/ MS_PER_H;
        long minutes = (timeSpent - hours* MS_PER_H)/ MS_PER_MIN;
        long seconds = (timeSpent - hours* MS_PER_H - minutes* MS_PER_MIN)/1000;

        b.append(hours).append("h ").append(minutes).append("m ").append(seconds).append('s');

        return b;
    }

    /**
     * Formats an amount of time as in: "27:12"
     * @param milliseconds time in milliseconds
     * @return pretty string
     */
    public static String formatClock(long milliseconds) {
        long minutes = milliseconds / (1000*60);
        long seconds = (milliseconds - minutes*60*1000)/1000;

        StringBuilder sb = new StringBuilder();
        if (minutes < 10) {
            sb.append('0');
        }
        sb.append(minutes);
        sb.append(':');
        if (seconds < 10) {
            sb.append('0');
        }
        sb.append(seconds);

        return sb.toString();
    }

    public static String getComment(Context context, Task task) {
        return new StringBuilder()
                .append(task.pomodoros)
                .append(context.getString(R.string.pomodoros))
                .append(", ")
                .append(formatTimeSpent(task.timeSpent))
                .append(context.getString(R.string.total_spent))
                .toString();
    }
}
