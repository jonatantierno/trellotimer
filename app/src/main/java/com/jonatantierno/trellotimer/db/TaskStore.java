package com.jonatantierno.trellotimer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jonatantierno.trellotimer.Task;

/**
 * This class gives access to the stored information about the tasks.
 * Created by jonatan on 21/04/15.
 */
public class TaskStore {
    final TasksDbHelper mDbHelper;
    public TaskStore(Context context){
        mDbHelper= new TasksDbHelper(context);
    }
    public Task getTask(String taskId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TrackEntry._ID,
                TrackEntry.COLUMN_NAME_ID,
                TrackEntry.COLUMN_NAME_NAME,
                TrackEntry.COLUMN_NAME_POMODOROS,
                TrackEntry.COLUMN_NAME_SECONDS_SPENT
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                TrackEntry.COLUMN_NAME_ID + " DESC";

        Cursor c = db.query(
                TrackEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TrackEntry.COLUMN_NAME_ID + " LIKE ?",     // The columns for the WHERE clause
                new String[]{taskId},                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.moveToFirst();

        return new Task(
                c.getString(c.getColumnIndexOrThrow(TrackEntry.COLUMN_NAME_ID)),
                c.getString(c.getColumnIndexOrThrow(TrackEntry.COLUMN_NAME_NAME)),
                c.getInt(c.getColumnIndexOrThrow(TrackEntry.COLUMN_NAME_POMODOROS)),
                c.getLong(c.getColumnIndexOrThrow(TrackEntry.COLUMN_NAME_SECONDS_SPENT))
                );
    }

    public void putTask(Task task){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TrackEntry.COLUMN_NAME_ID, task.id);
        values.put(TrackEntry.COLUMN_NAME_NAME, task.name);
        values.put(TrackEntry.COLUMN_NAME_POMODOROS, task.pomodoros);
        values.put(TrackEntry.COLUMN_NAME_SECONDS_SPENT, task.secondsSpent);

        // Insert the new row, returning the primary key value of the new row
        db.insert(TrackEntry.TABLE_NAME,null,values);
    }
}
