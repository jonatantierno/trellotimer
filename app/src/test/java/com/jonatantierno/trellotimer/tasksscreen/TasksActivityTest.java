package com.jonatantierno.trellotimer.tasksscreen;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.jonatantierno.trellotimer.BuildConfig;
import com.jonatantierno.trellotimer.CredentialFactory;
import com.jonatantierno.trellotimer.MainActivity;
import com.jonatantierno.trellotimer.MainActivityTest;
import com.jonatantierno.trellotimer.R;
import com.jonatantierno.trellotimer.configscreens.SelectBoardActivity;
import com.jonatantierno.trellotimer.StatusStore;
import com.jonatantierno.trellotimer.TTApplication;
import com.jonatantierno.trellotimer.trellorequests.TTCallback;
import com.jonatantierno.trellotimer.trellorequests.TTConnections;
import com.jonatantierno.trellotimer.db.TaskStore;
import com.jonatantierno.trellotimer.model.ListType;
import com.jonatantierno.trellotimer.model.Task;
import com.jonatantierno.trellotimer.timerscreen.TimerActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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
public class TasksActivityTest {

    public static final String CARD_NAME = "card_name";
    public static final String CARD_ID = "card_id";

    TasksActivity activity;
    ActivityController<TasksActivity> activityController;
    TTConnections connections = mock(TTConnections.class);
    StatusStore store = mock(StatusStore.class);
    TaskStore taskStore = mock(TaskStore.class);
    CredentialFactory credentialFactory = mock(CredentialFactory.class);

    TTApplication application;
    private Task task;
    private TasksFragment todoFragment;
    private TasksFragment doingFragment;
    private View selectedView;

    @Before
    public void setup(){
        activityController = Robolectric.buildActivity(TasksActivity.class);
        activity = activityController.get();

        application = (TTApplication) RuntimeEnvironment.application;

        application.credentialFactory= credentialFactory;
        application.connections = connections;
        application.store = store;
        application.taskStore = taskStore;

        task = new Task(CARD_ID, CARD_NAME,7,90*1000);
        selectedView = mock(View.class);

        activity.testing = true;

        activityController.create();

        todoFragment = getFragment(ListType.TODO);
        doingFragment = getFragment(ListType.DOING);
    }

    @Test
    public void whenTodoTaskPressedThenMoveToDoing(){
        todoFragment.list.add(task);
        todoFragment.onItemRight(0, selectedView);

        verify(connections).moveToList(eq(CARD_ID), eq(ListType.DOING), eq(credentialFactory), any(TTCallback.class));

        todoFragment.activity.onTaskMoved(0, ListType.TODO, ListType.DOING);
        assertFalse(todoFragment.list.contains(task));
        assertTrue(doingFragment.list.contains(task));
    }

    @Test
    public void whenPressedThenNothingElseHappensTillMovementFinished(){
        todoFragment.list.add(task);

        todoFragment.onItemRight(0, selectedView);
        todoFragment.onItemLeft(0, selectedView);
        doingFragment.onItemLeft(0, selectedView);
        doingFragment.onItemRight(0, selectedView);

        assertEquals(View.VISIBLE, activity.progressBar.getVisibility());
        verify(connections, times(1)).moveToList(eq(CARD_ID), eq(ListType.DOING), eq(credentialFactory), any(TTCallback.class));

        // Untill onTaskMoved nothing else happens.
        todoFragment.activity.onTaskMoved(0, ListType.TODO, ListType.DOING);

        assertEquals(View.GONE, activity.progressBar.getVisibility());


    }

    @Test
    public void whenPressDoingThenGoToTimerScreen(){
        doingFragment.list.add(task);
        doingFragment.onItemSelected(0, selectedView);

        verify(taskStore).putTask(new Task(CARD_ID, CARD_NAME, 0, 0));


        Intent nextActivity = Shadows.shadowOf(activity).getNextStartedActivity();

        assertNotNull(nextActivity);
        assertEquals(CARD_ID, nextActivity.getStringExtra(TasksActivity.EXTRA_CARD_ID));
        assertEquals(TimerActivity.class.getCanonicalName(), nextActivity.getComponent().getClassName());
        assertFalse(activity.isFinishing());
    }


    private TasksFragment getFragment(ListType type) {
        TasksFragment fragment = activity.fragments[type.ordinal()];
        fragment.listType = type;
        fragment.activity = activity;
        return fragment;
    }


    @Test
    public void whenConfigureListThenGoToSelectBoard(){
        MenuItem deleteCredentialsItem = mock(MenuItem.class);
        when(deleteCredentialsItem.getItemId()).thenReturn(R.id.action_configure_lists);

        activity.onOptionsItemSelected(deleteCredentialsItem);

        MainActivityTest.checkGoneTo(activity, SelectBoardActivity.class);
    }

    @Test
    public void whenDeleteCredentialsThenGoToFirstScreen(){
        MenuItem deleteCredentialsItem = mock(MenuItem.class);
        when(deleteCredentialsItem.getItemId()).thenReturn(R.id.action_delete_data);

        activity.onOptionsItemSelected(deleteCredentialsItem);

        verify(taskStore).clear();
        verify(credentialFactory).deleteCredentials();
        verify(store).reset();
        MainActivityTest.checkGoneTo(activity,MainActivity.class);

    }
}