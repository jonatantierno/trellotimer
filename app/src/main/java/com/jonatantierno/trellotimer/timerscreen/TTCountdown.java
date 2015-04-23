package com.jonatantierno.trellotimer.timerscreen;

import com.jonatantierno.trellotimer.Formatter;

/**
 * This class handles the countdown time for the pomodoro clock.
 * Most methods receive the current time as a parameter so as to
 * allow for time -independent tests.
 * Created by jonatan on 22/04/15.
 */
public class TTCountdown {
    final TimerActivity activity;
    Ticker ticker;
    long timeout = 0;
    long initialTime = 0;
    long timeLeft = 0;
    long startOfLastPeriod = 0;

    long lastTick;
    private boolean paused = true;


    public TTCountdown(TimerActivity timerActivity) {
        this(timerActivity,new Ticker());
        this.ticker.clock = this;
    }
    // Constructor used in tests, to inject mock Ticker
    TTCountdown(TimerActivity activity, Ticker ticker) {
        this.activity = activity;
        this.ticker = ticker;
    }


    public void start(long timeout,long currentTime) {
        prepareStart(timeout, currentTime);
        ticker.tickInAsecond();
    }

    private void prepareStart(long timeout, long initialTIme) {
        this.paused = false;
        this.timeout = this.timeLeft = timeout;
        this.initialTime = this.lastTick = this.startOfLastPeriod = initialTIme;
    }

    public void pause(long currentTime) {
        if (paused){
            return;
        }
        paused = true;

        processTime(currentTime);
    }

    public void unpause(long currentTime) {
        paused = false;

        lastTick = startOfLastPeriod = currentTime;
        ticker.tickInAsecond();
    }

    /**
     * Returns time elapsed since last pause.
     * @return time in milliseconds
     */
    public long getElapsedTime() {
        return lastTick - startOfLastPeriod;
    }

    public boolean isPaused() {
        return paused;
    }


    void tick(long currentTime) {
        if (paused){
            return;
        }

        processTime(currentTime);

        if (timeLeft > 0){
            ticker.tickInAsecond();
        }
    }


    private void processTime(long currentTime) {
        long timeElapsed= (currentTime - lastTick);
        timeLeft -= timeElapsed;
        lastTick = currentTime;

        if (timeLeft <= 0) {
            activity.timeUp();
            paused = true;
        } else {
            activity.showTime(Formatter.formatClock(timeLeft));
        }
    }
}

