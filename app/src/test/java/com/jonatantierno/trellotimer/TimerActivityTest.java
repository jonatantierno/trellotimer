package com.jonatantierno.trellotimer;

import android.content.Intent;
import android.view.View;

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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by jonatan on 15/04/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class TimerActivityTest {

    private static final String CARD_ID = "card_id";
    public static final String CARD_NAME = "card_name";
    public static final long TIME_SPENT = 90 *1000;
    public static final int POMODOROS = 7;
    public static final long THIRTY_MINUTES = 30 * 60 * 1000;

    TimerActivity activity;
    ActivityController<TimerActivity> activityController;
    StatusStore store = mock(StatusStore.class);
    TaskStore taskStore = mock(TaskStore.class);
    CredentialFactory credentialFactory = mock(CredentialFactory.class);
    TTApplication application;
    private Task currentTask;

    @Before
    public void setup() throws IOException {
        activityController = Robolectric.buildActivity(TimerActivity.class);
        activity = activityController.get();

        activity.clock = mock(TTClock.class);


        application = (TTApplication) RuntimeEnvironment.application;

        application.store = store;
        application.credentialFactory= credentialFactory;
        application.taskStore = taskStore;

        currentTask = new Task(CARD_ID, CARD_NAME, POMODOROS, TIME_SPENT);

        when(activity.clock.getElapsedTime()).thenReturn(THIRTY_MINUTES);
        when(taskStore.getTask(CARD_ID)).thenReturn(currentTask);
        when(credentialFactory.getCredential()).thenReturn(mock(Credential.class));

        final Intent intent = new Intent().putExtra(TasksActivity.EXTRA_CARD_ID,CARD_ID);
        activityController.withIntent(intent).create().resume();
    }

    @Test
    public void shouldShowSelectedTask(){
        verify(taskStore).getTask(CARD_ID);

        assertEquals(CARD_NAME, activity.taskNameTextView.getText().toString());
        assertEquals(POMODOROS + activity.getString(R.string.pomodoros), activity.pomodorosTextView.getText().toString());
        assertEquals("0h 1m 30s", activity.secondsSpentTextView.getText().toString());
    }

    @Test
    public void shouldShowTime(){
        activity.showTime("30:00");

        assertEquals("30:00", activity.timeTextView.getText().toString());

    }
    @Test
    public void whenStartTimerShouldShowControls(){
        activity.pomodoroButton.performClick();

        verify(activity.clock).start(eq(TimerActivity.THIRTY_MINUTES),anyLong());

        assertEquals(View.VISIBLE, activity.controlClockLayout.getVisibility());
        assertEquals(View.GONE, activity.startClockLayout.getVisibility());
    }


    @Test
    public void whenPauseButtonShouldPauseClockAndStoreTaskData(){
        activity.pauseButton.performClick();

        verify(activity.clock).pause(anyLong());
        assertEquals(activity.getString(R.string.continue_button), activity.pauseButton.getText().toString());

        verify(taskStore).updateTask(currentTask.increaseTaskTime(THIRTY_MINUTES));
    }

    @Test
    public void whenUnpauseShouldContinue(){
        when(activity.clock.isPaused()).thenReturn(false).thenReturn(true);

        activity.pauseButton.performClick();
        activity.pauseButton.performClick();

        verify(activity.clock).unpause(anyLong());
        assertEquals(activity.getString(R.string.pause), activity.pauseButton.getText().toString());
    }


    @Test
    public void whenPauseShouldPauseClockAndStoreTaskData(){
        activityController.pause();

        verify(activity.clock).pause(anyLong());
        verify(taskStore).updateTask(currentTask.increaseTaskTime(THIRTY_MINUTES));
    }

    @Test
    public void whenTimeUpHideControlsShowStartsStoreTaskData(){
        activity.pomodoroButton.performClick();

        activity.timeUp();

        assertEquals(View.VISIBLE, activity.startClockLayout.getVisibility());
        assertEquals(View.GONE, activity.controlClockLayout.getVisibility());

        verify(taskStore).updateTask(currentTask.increaseTaskPomodoros(THIRTY_MINUTES));
    }

    @Test
    public void shouldFormat(){
        assertEquals("17h 1m 30s", TimerActivity.format((17 * 60 * 60 + 1*60 +30)*1000));

    }

}