package com.jonatantierno.trellotimer.db;

import android.provider.BaseColumns;

/* Inner class that defines the table contents */
public abstract class TrackEntry implements BaseColumns {
    public static final String TABLE_NAME = "task";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_POMODOROS= "pomodoros";
    public static final String COLUMN_NAME_SECONDS_SPENT= "secondsSpent";
}