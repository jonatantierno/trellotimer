package com.jonatantierno.trellotimer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jonatan on 21/04/15.
 */
public class Task extends Item{
    public final int pomodoros;
    public final long secondsSpent;
    public Task(String id, String name, int pomodoros, long secondsSpent) {
        super(id, name);
        this.pomodoros = pomodoros;
        this.secondsSpent = secondsSpent;
    }

    public Task(Item item){
        this(item.id,item.name,0,0);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task)){
            return true;
        }
        Task task = (Task)o;

        return id.equals(task.id) &&
                name.equals(task.name) &&
                pomodoros == task.pomodoros &&
                secondsSpent == task.secondsSpent;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + id.hashCode() >>> 3 + pomodoros >>> 2 + secondsSpent >>> 1;
    }
}
