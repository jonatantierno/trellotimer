package com.jonatantierno.trellotimer;

import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by jonatan on 15/04/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class MainActivityTest {

    @Test
    public void smokeTest(){
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();

        assertEquals("Hello world!",((TextView)activity.findViewById(R.id.textview)).getText().toString());
    }
}