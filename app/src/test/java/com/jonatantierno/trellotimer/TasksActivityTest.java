package com.jonatantierno.trellotimer;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.jonatantierno.trellotimer.db.TaskStore;

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
    public static final String CARD_ID_2 = "card_id_2";

    TasksActivity activity;
    ActivityController<TasksActivity> activityController;
    TTConnections connections = mock(TTConnections.class);
    StatusStore store = mock(StatusStore.class);
    TaskStore taskStore = mock(TaskStore.class);

    CredentialFactory credentialFactory = mock(CredentialFactory.class);

    TTApplication application;
    @Before
    public void setup(){
        activityController = Robolectric.buildActivity(TasksActivity.class);
        activity = activityController.get();

        application = (TTApplication) RuntimeEnvironment.application;

        application.credentialFactory= credentialFactory;
        application.connections = connections;
        application.store = store;
        application.taskStore = taskStore;

        activity.testing = true;
    }

    @Test
    public void whenTodoTaskPressedThenMoveToDoing(){
        activityController.create();

        TasksFragment todoFragment = getFragment(ListType.TODO);
        TasksFragment doingFragment = getFragment(ListType.DOING);

        final View selectedView = mock(View.class);

        Item card= new Item(CARD_ID, CARD_NAME);

        todoFragment.list.add(card);
        todoFragment.onItemRight(0, selectedView);

        verify(connections).moveToList(eq(CARD_ID), eq(ListType.DOING), eq(credentialFactory), any(TTCallback.class));

        todoFragment.activity.onTaskMoved(0, ListType.TODO, ListType.DOING);
        assertFalse(todoFragment.list.contains(card));
        assertTrue(doingFragment.list.contains(card));

    }

    @Test
    public void whenPressedThenNothingElseHappensTillMovementFinished(){
        activityController.create();

        TasksFragment todoFragment = getFragment(ListType.TODO);
        TasksFragment doingFragment = getFragment(ListType.DOING);

        final View selectedView = mock(View.class);

        Item card= new Item(CARD_ID, CARD_NAME);

        todoFragment.list.add(card);
        todoFragment.onItemRight(0, selectedView);

        todoFragment.onItemRight(0, selectedView);
        todoFragment.onItemLeft(0, selectedView);
        doingFragment.onItemLeft(0, selectedView);
        doingFragment.onItemRight(0, selectedView);

        assertEquals(View.VISIBLE, activity.progressBar.getVisibility());
        verify(connections,times(1)).moveToList(eq(CARD_ID), eq(ListType.DOING), eq(credentialFactory), any(TTCallback.class));

        // Untill onTaskMoved nothing else happens.
        todoFragment.activity.onTaskMoved(0, ListType.TODO, ListType.DOING);

        assertEquals(View.GONE, activity.progressBar.getVisibility());


    }

    @Test
    public void whenPressDoingThenGoToTimerScreen(){
        activityController.create();

        TasksFragment doingFragment = getFragment(ListType.DOING);

        final View selectedView = mock(View.class);

        Item card= new Item(CARD_ID, CARD_NAME);

        doingFragment.list.add(card);
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
        activityController.create();

        MenuItem deleteCredentialsItem = mock(MenuItem.class);
        when(deleteCredentialsItem.getItemId()).thenReturn(R.id.action_configure_lists);

        activity.onOptionsItemSelected(deleteCredentialsItem);

        MainActivityTest.checkGoneTo(activity, SelectBoardActivity.class);
    }

    @Test
    public void whenDeleteCredentialsThenGoToFirstScreen(){
        activityController.create();

        MenuItem deleteCredentialsItem = mock(MenuItem.class);
        when(deleteCredentialsItem.getItemId()).thenReturn(R.id.action_delete_data);

        activity.onOptionsItemSelected(deleteCredentialsItem);

        verify(taskStore).clear();
        verify(credentialFactory).deleteCredentials();
        verify(store).reset();
        MainActivityTest.checkGoneTo(activity,MainActivity.class);

    }

    @Test
    public void whenReceivedCardToFinishThenMoveItAndGoToDone(){
        final Intent intent = new Intent().putExtra(TasksActivity.EXTRA_CARD_ID_TO_FINISH, CARD_ID_2);

        activityController.withIntent(intent).create().resume();

        assertEquals(View.VISIBLE, activity.progressBar.getVisibility());
        verify(connections, times(1)).moveToList(eq(CARD_ID_2), eq(ListType.DONE), eq(credentialFactory), any(TTCallback.class));

        assertEquals(ListType.DONE.ordinal(),activity.mViewPager.getCurrentItem());
    }


}