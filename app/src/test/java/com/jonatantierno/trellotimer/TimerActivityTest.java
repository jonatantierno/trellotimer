package com.jonatantierno.trellotimer;

import android.content.Intent;

import com.google.api.client.auth.oauth2.Credential;
import com.jonatantierno.trellotimer.db.TaskStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jonatan on 15/04/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class TimerActivityTest {

    private static final String CARD_ID = "card_id";
    public static final String CARD_NAME = "card_name";
    public static final int SECONDS_SPENT = 90;
    public static final int POMODOROS = 7;
    TimerActivity activity;
    ActivityController<TimerActivity> activityController;
    StatusStore store = mock(StatusStore.class);
    TTConnections connections = mock(TTConnections.class);
    TaskStore taskStore = mock(TaskStore.class);
    CredentialFactory credentialFactory = mock(CredentialFactory.class);
    TTApplication application;

    @Before
    public void setup() throws IOException {
        activityController = Robolectric.buildActivity(TimerActivity.class);
        activity = activityController.get();

        application = (TTApplication) RuntimeEnvironment.application;

        application.store = store;
        application.credentialFactory= credentialFactory;
        application.taskStore = taskStore;

        when(taskStore.getTask(CARD_ID)).thenReturn(new Task(CARD_ID, CARD_NAME, POMODOROS, SECONDS_SPENT));
        when(credentialFactory.getCredential()).thenReturn(mock(Credential.class));

        final Intent intent = new Intent().putExtra(TasksActivity.EXTRA_CARD_ID,CARD_ID);
        activityController.withIntent(intent);
    }

    @Test
    public void shouldShowSelectedTask(){
        activityController.create().resume();

        verify(taskStore).getTask(CARD_ID);

        assertEquals(CARD_NAME, activity.taskNameTextView.getText().toString());
        assertEquals(POMODOROS + activity.getString(R.string.pomodoros), activity.pomodorosTextView.getText().toString());
        assertEquals("0h 1m 30s", activity.secondsSpentTextView.getText().toString());
    }

    @Test
    public void shouldShowTime(){
        activityController.create().resume();

        activity.showTime("30:00");

        assertEquals("30:00", activity.timeTextView.getText().toString());

    }


}