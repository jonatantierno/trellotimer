package com.jonatantierno.trellotimer.timerscreen;

import android.os.Handler;

/**
 * Class that performs the one-second wait. Extracted so it can be
 * mocked in tests.
 * Created by jonatan on 23/04/15.
 */
class Ticker {
    public static final int ONE_SECOND = 1000;

    private final Handler handler = new Handler();
    TTCountdown clock;

    public void tickInAsecond(){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clock.tick(System.currentTimeMillis());
                }
            }, ONE_SECOND);
    }
}
