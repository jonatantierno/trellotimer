package com.jonatantierno.trellotimer;

import android.app.Fragment;
import android.view.MenuItem;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;
import org.robolectric.util.FragmentTestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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
public class TasksActivityTest {

    public static final String CARD_NAME = "card_name";
    public static final String CARD_ID = "card_id";
    TasksActivity activity;
    ActivityController<TasksActivity> activityController;
    TTConnections connections = mock(TTConnections.class);
    StatusStore store = mock(StatusStore.class);
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

        activity.testing = true;
        activityController.create();

    }

    @Test
    public void whenTodoTaskPressedThenMoveToDoing(){
        TasksFragment todoFragment = getFragment(ListType.TODO);
        TasksFragment doingFragment = getFragment(ListType.DOING);

        final View selectedView = mock(View.class);

        Item card= new Item(CARD_ID, CARD_NAME);

        todoFragment.list.add(card);
        todoFragment.onItemRight(0, selectedView);

        verify(connections).moveToList(eq(CARD_ID), eq(ListType.DOING), eq(credentialFactory), any(TTCallback.class));

        todoFragment.onTaskMoved(0,ListType.DOING);
        assertFalse(todoFragment.list.contains(card));
        assertTrue(doingFragment.list.contains(card));

    }

    @Test
    public void whenPressedThenNothingElseHappensTillMovementFinished(){
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
        todoFragment.onTaskMoved(0,ListType.DOING);

        assertEquals(View.GONE, activity.progressBar.getVisibility());


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

        MainActivityTest.checkGoneTo(activity,SelectBoardActivity.class);
    }

    @Test
    public void whenDeleteCredentialsThenGoToFirstScreen(){
        MenuItem deleteCredentialsItem = mock(MenuItem.class);
        when(deleteCredentialsItem.getItemId()).thenReturn(R.id.action_delete_credentials);

        activity.onOptionsItemSelected(deleteCredentialsItem);

        verify(credentialFactory).deleteCredentials();
        verify(store).reset();
        MainActivityTest.checkGoneTo(activity,MainActivity.class);

    }


}