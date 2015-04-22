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
                TaskEntry._ID,
                TaskEntry.COLUMN_NAME_ID,
                TaskEntry.COLUMN_NAME_NAME,
                TaskEntry.COLUMN_NAME_POMODOROS,
                TaskEntry.COLUMN_NAME_TIME_SPENT
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                TaskEntry.COLUMN_NAME_ID + " DESC";

        Cursor c = db.query(
                TaskEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskEntry.COLUMN_NAME_ID + " LIKE ?",     // The columns for the WHERE clause
                new String[]{taskId},                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.moveToFirst();

        return new Task(
                c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ID)),
                c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_NAME)),
                c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_POMODOROS)),
                c.getLong(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TIME_SPENT))
                );
    }

    public void putTask(Task task){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_ID, task.id);
        values.put(TaskEntry.COLUMN_NAME_NAME, task.name);
        values.put(TaskEntry.COLUMN_NAME_POMODOROS, task.pomodoros);
        values.put(TaskEntry.COLUMN_NAME_TIME_SPENT, task.timeSpent);

        // Insert the new row, returning the primary key value of the new row
        db.insert(TaskEntry.TABLE_NAME, null, values);
    }

    public void updateTask(Task task) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_POMODOROS, task.pomodoros);
        values.put(TaskEntry.COLUMN_NAME_TIME_SPENT, task.timeSpent);

        // Which row to update, based on the ID
        String selection = TaskEntry.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(task.id) };

        // Insert the new row, returning the primary key value of the new row
        db.update(TaskEntry.TABLE_NAME, values,selection,selectionArgs);

    }
}
