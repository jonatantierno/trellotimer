package com.jonatantierno.trellotimer.timerscreen;

import android.content.Context;
import android.media.MediaPlayer;

import com.jonatantierno.trellotimer.R;

/**
 * Simple class to play a sound.
 * Created by jonatan on 23/04/15.
 */
class TTSoundPlayer{
    public void play(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer.create(context, R.raw.airhorn).start();
            }
        }).start();
    }
}
