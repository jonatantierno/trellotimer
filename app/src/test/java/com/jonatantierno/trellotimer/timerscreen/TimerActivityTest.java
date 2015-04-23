package com.jonatantierno.trellotimer.timerscreen;

import android.content.Intent;
import android.view.View;

import com.google.api.client.auth.oauth2.Credential;
import com.jonatantierno.trellotimer.BuildConfig;
import com.jonatantierno.trellotimer.CredentialFactory;
import com.jonatantierno.trellotimer.R;
import com.jonatantierno.trellotimer.StatusStore;
import com.jonatantierno.trellotimer.TTApplication;
import com.jonatantierno.trellotimer.trellorequests.TTCallback;
import com.jonatantierno.trellotimer.trellorequests.TTConnections;
import com.jonatantierno.trellotimer.tasksscreen.TasksActivity;
import com.jonatantierno.trellotimer.Formatter;
import com.jonatantierno.trellotimer.db.TaskStore;
import com.jonatantierno.trellotimer.model.Task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
    public static final long TIME_SPENT = 90 *1000;
    public static final int POMODOROS = 7;
    public static final long THIRTY_MINUTES = 30 * 60 * 1000;
    private static final String COMMENT ="7"+"\\u0020"+" pomodoros, 0h 1m 30s"+"\\u0020"+" total spent";

    TimerActivity activityUnderTest;
    ActivityController<TimerActivity> activityController;
    StatusStore store = mock(StatusStore.class);
    TaskStore taskStore = mock(TaskStore.class);
    CredentialFactory credentialFactory = mock(CredentialFactory.class);
    TTApplication application;
    private Task currentTask;

    @Before
    public void setup() throws IOException {
        activityController = Robolectric.buildActivity(TimerActivity.class);
        activityUnderTest = activityController.get();

        activityUnderTest.clock = mock(TTCountdown.class);
        activityUnderTest.player = mock(TTSoundPlayer.class);

        activityUnderTest.inABreak = false;

        application = (TTApplication) RuntimeEnvironment.application;

        application.store = store;
        application.connections = mock(TTConnections.class);
        application.credentialFactory= credentialFactory;
        application.taskStore = taskStore;

        currentTask = new Task(CARD_ID, CARD_NAME, POMODOROS, TIME_SPENT);

        when(activityUnderTest.clock.getElapsedTime()).thenReturn(THIRTY_MINUTES);
        when(taskStore.getTask(CARD_ID)).thenReturn(currentTask);
        when(credentialFactory.getCredential()).thenReturn(mock(Credential.class));

        final Intent intent = new Intent().putExtra(TasksActivity.EXTRA_CARD_ID,CARD_ID);
        activityController.withIntent(intent).create().resume();
    }

    @Test
    public void shouldShowSelectedTask(){
        verify(taskStore).getTask(CARD_ID);

        checkTaskDataShown(currentTask);
    }

    private void checkTaskDataShown(Task task) {
        assertEquals(task.name, activityUnderTest.taskNameTextView.getText().toString());
        assertEquals(task.pomodoros + activityUnderTest.getString(R.string.pomodoros), activityUnderTest.pomodorosTextView.getText().toString());
        Assert.assertEquals(Formatter.formatTimeSpent(task.timeSpent), activityUnderTest.timeSpentTextView.getText().toString());
    }

    @Test
    public void shouldShowTime(){
        activityUnderTest.showTime("30:00");

        assertEquals("30:00", activityUnderTest.clockTextView.getText().toString());

    }
    @Test
    public void whenStartPomodoroShouldShowControls(){
        activityUnderTest.inABreak = true; // To check that the value changes with the button.
        activityUnderTest.taskNameTextView.setText("");

        activityUnderTest.doneButton.setVisibility(View.VISIBLE);

        activityUnderTest.pomodoroButton.performClick();

        verify(activityUnderTest.clock).start(eq(TimerActivity.POMODORO_DURATION), anyLong());
        checkTaskDataShown(currentTask);

        checkShowControls();

        assertEquals("30:00", activityUnderTest.clockTextView.getText().toString());

        assertFalse(activityUnderTest.inABreak);

    }

    private void checkShowControls() {
        assertEquals(View.VISIBLE, activityUnderTest.controlClockLayout.getVisibility());
        assertEquals(View.GONE, activityUnderTest.startClockLayout.getVisibility());
        assertEquals(View.GONE, activityUnderTest.doneButton.getVisibility());
    }

    @Test
    public void whenStartShortBreakShouldShowControls(){
        activityUnderTest.doneButton.setVisibility(View.VISIBLE);

        activityUnderTest.shortBreakButton.performClick();

        verify(activityUnderTest.clock).start(eq(TimerActivity.SHORT_BREAK_DURATION), anyLong());

        checkShowControls();

        assertEquals("05:00", activityUnderTest.clockTextView.getText().toString());

        checkBreak();
    }

    @Test
    public void whenStartLongBreakShouldShowControls(){
        activityUnderTest.doneButton.setVisibility(View.VISIBLE);

        activityUnderTest.longBreakButton.performClick();

        verify(activityUnderTest.clock).start(eq(TimerActivity.LONG_BREAK_DURATION), anyLong());

        checkShowControls();

        assertEquals("15:00", activityUnderTest.clockTextView.getText().toString());

        checkBreak();
    }

    private void checkBreak() {
        verify(taskStore, times(0)).putTask(any(Task.class));

        assertEquals(activityUnderTest.getString(R.string.break_text), activityUnderTest.taskNameTextView.getText().toString());
        assertEquals("", activityUnderTest.pomodorosTextView.getText().toString());
        assertEquals("", activityUnderTest.timeSpentTextView.getText().toString());

        assertTrue(activityUnderTest.inABreak);
    }

    @Test
    public void whenResumeTaskThouldHideDoneButton(){
        activityController.resume().pause().resume();

        assertEquals(View.GONE, activityUnderTest.doneButton.getVisibility());
    }

    @Test
    public void whenPauseButtonShouldPauseClockAndStoreTaskData(){
        activityUnderTest.pauseButton.performClick();

        checkPause(true);
    }

    @Test
    public void whenStopButtonShouldPauseClockAndStoreTaskData(){
        activityUnderTest.pauseButton.setText(""); // To check it changes...
        activityUnderTest.stopButton.performClick();

        checkPause(false);

        assertEquals("--:--", activityUnderTest.clockTextView.getText().toString());
        assertEquals(View.GONE, activityUnderTest.controlClockLayout.getVisibility());
        assertEquals(View.VISIBLE, activityUnderTest.startClockLayout.getVisibility());
        assertEquals(View.GONE, activityUnderTest.doneButton.getVisibility());

        assertEquals(activityUnderTest.getString(R.string.pause), activityUnderTest.pauseButton.getText().toString());

        final Task task = currentTask.increaseTaskTime(THIRTY_MINUTES);
        checkTaskDataShown(task);
    }

    @Test
    public void whenPauseShouldPauseClockAndStoreTaskData(){
        activityController.pause();

        checkPause(true);
    }

    @Test
    public void whenPauseAndInABreakShouldNotStoreData(){
        activityUnderTest.longBreakButton.performClick();

        activityController.pause();

        checkBreakPause();
    }
    private void checkBreakPause() {
        verify(activityUnderTest.clock).pause(anyLong());
        assertEquals(activityUnderTest.getString(R.string.continue_button), activityUnderTest.pauseButton.getText().toString());

        checkBreak();
    }

    private void checkPause(boolean checkButtonText) {
        verify(activityUnderTest.clock).pause(anyLong());

        if (checkButtonText) {
            assertEquals(activityUnderTest.getString(R.string.continue_button), activityUnderTest.pauseButton.getText().toString());
        }

        final Task task = currentTask.increaseTaskTime(THIRTY_MINUTES);
        verify(taskStore).updateTask(task);
        checkTaskDataShown(task);
    }

    @Test
    public void whenUnpauseShouldContinue(){
        when(activityUnderTest.clock.isPaused()).thenReturn(false).thenReturn(true);

        activityUnderTest.pauseButton.performClick();
        activityUnderTest.pauseButton.performClick();

        verify(activityUnderTest.clock).unpause(anyLong());
        assertEquals(activityUnderTest.getString(R.string.pause), activityUnderTest.pauseButton.getText().toString());
    }



    @Test
    public void whenTimeUpHideControlsShowStartsStoreTaskDataPlaySound(){
        activityUnderTest.pomodoroButton.performClick();

        activityUnderTest.timeUp();

        assertEquals(View.VISIBLE, activityUnderTest.startClockLayout.getVisibility());
        assertEquals(View.VISIBLE, activityUnderTest.doneButton.getVisibility());
        assertEquals(View.GONE, activityUnderTest.controlClockLayout.getVisibility());

        final Task task = currentTask.increaseTaskPomodoros(THIRTY_MINUTES);
        verify(taskStore).updateTask(task);

        checkTaskDataShown(task);

        verify(activityUnderTest.player).play(activityUnderTest);
    }

    @Test
    public void whenTaskDoneThenResetClockAndMoveTask(){
        activityUnderTest.doneButton.performClick();

        verify(application.connections).addComment(eq(CARD_ID), eq(COMMENT), eq(credentialFactory), any(TTCallback.class));

        assertEquals(View.GONE, activityUnderTest.doneButton.getVisibility());


        Intent nextActivity = Shadows.shadowOf(activityUnderTest).getNextStartedActivity();

        assertNotNull(nextActivity);
        assertEquals(CARD_ID, nextActivity.getStringExtra(TasksActivity.EXTRA_CARD_ID_TO_FINISH));
        assertEquals(TasksActivity.class.getCanonicalName(), nextActivity.getComponent().getClassName());
        assertTrue(activityUnderTest.isFinishing());
    }

    @Test
    public void shouldFormat(){
        assertEquals("17h 1m 30s", Formatter.formatTimeSpent((17 * 60 * 60 + 1 * 60 + 30) * 1000));

    }
}