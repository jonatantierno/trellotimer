package com.jonatantierno.trellotimer.model;

import com.jonatantierno.trellotimer.Formatter;

/**
 * This Immutable class represents a Trello card containing a task.
 * Created by jonatan on 21/04/15.
 */
public class Task extends Item {
    public final int pomodoros;
    public final long timeSpent;

    public Task(String id, String name, int pomodoros, long timeSpent) {
        super(id, name, buildInfo(pomodoros, timeSpent));

        this.pomodoros = pomodoros;
        this.timeSpent = timeSpent;
    }

    private static String buildInfo(int pomodoros, long timeSpent) {
        if (pomodoros == 0 && timeSpent == 0){
            return null;
        }
        return Formatter.formatTimeSpent(timeSpent, new StringBuilder())
                .append(' ')
                .append('(')
                .append(pomodoros)
                .append(')')
                .toString();
    }

    public Task(Item item){
        this(item.id,item.name,0,0);
    }

    public Task increaseTaskPomodoros(long pomodoroTime){
        return new Task(id,name,pomodoros+1, timeSpent +pomodoroTime);
    }
    public Task increaseTaskTime(long pomodoroTime) {
        return new Task(id,name,pomodoros, timeSpent +pomodoroTime);
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
                timeSpent == task.timeSpent;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + id.hashCode() >>> 3 + pomodoros >>> 2 + timeSpent >>> 1;
    }

    @Override
    public String toString() {
        return name + ", " + pomodoros + "p, " + timeSpent +"ms";
    }

}
