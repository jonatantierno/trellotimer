package com.jonatantierno.trellotimer;

import android.content.Intent;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by jonatan on 15/04/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class ConfigActivityTest {

    ConfigActivity activity;
    ActivityController<SelectBoardActivity> activityController;

    @Before
    public void setup(){
        activityController = Robolectric.buildActivity(SelectBoardActivity.class);
        activity = activityController.get();
    }

    @Test
    public void whenFailureThenShowErrorMessage(){
        activityController.create();

        activity.failure(new Exception("Exception"));

        assertEquals(activity.getString(R.string.connection_error), activity.messageTextView.getText().toString());
        assertEquals(View.GONE, activity.progressBar.getVisibility());
    }
}