package com.jonatantierno.trellotimer;

import android.os.Handler;

/**
 * This class handles the time and the Alarm manager for the alarms.
 * Created by jonatan on 22/04/15.
 */
public class TTClock {
    final TimerActivity activity;
    Ticker ticker;
    long timeout = 0;
    long initialTime = 0;
    long timeLeft = 0;

    long lastTick;
    private boolean paused = false;

    TTClock(TimerActivity activity, Ticker ticker) {
        this.activity = activity;
        this.ticker = ticker;
    }

    public TTClock(TimerActivity timerActivity) {
        this(timerActivity,new Ticker());
        this.ticker.clock = this;
    }

    public void start(int timeout,long currentTime) {
        prepareStart(timeout, currentTime);
        ticker.tickInAsecond();
    }

    private void prepareStart(long timeout, long initialTIme) {
        this.paused = false;
        this.timeout = this.timeLeft = timeout;
        this.initialTime = this.lastTick = initialTIme;
    }

    public void tick(long currentTime) {
        if (paused){
            return;
        }

        processTime(currentTime);

        if (timeLeft > 0){
            ticker.tickInAsecond();
        }
    }
    public void pause(long currentTime) {
        if (paused){
            return;
        }
        paused = true;

        processTime(currentTime);

    }

    private void processTime(long currentTime) {
        long timeElapsed= (currentTime - lastTick);
        timeLeft -= timeElapsed;
        lastTick = currentTime;

        if (timeLeft <= 0) {
            activity.timeUp();
        } else {
            activity.showTime(TTClock.format(timeLeft));
        }
    }

    public static String format(long milliseconds) {
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

    public void unpause(long currentTime) {
        paused = false;

        lastTick = currentTime;
        ticker.tickInAsecond();
    }

    public long getElapsedTime() {
        return timeout-timeLeft;
    }

    public boolean isPaused() {
        return paused;
    }
}

class Ticker {
    public static final int ONE_SECOND = 1000;

    private final Handler handler = new Handler();
    TTClock clock;

    public void tickInAsecond(){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clock.tick(System.currentTimeMillis());
                }
            }, ONE_SECOND);
    }
}

