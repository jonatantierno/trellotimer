package com.jonatantierno.trellotimer;

import android.app.Fragment;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    }


    @Test
    public void whenTodoTaskPressedThenMoveToDoing(){
        activity.testing = true;
        activityController.create();

        TasksFragment todoFragment = getFragment(ListType.TODO);
        TasksFragment doingFragment = getFragment(ListType.DOING);
        TasksFragment doneFragment = getFragment(ListType.DONE);

        final View selectedView = mock(View.class);

        Item card= new Item(CARD_ID, CARD_NAME);

        todoFragment.list.add(card);
        todoFragment.onItemRight(0, selectedView);

        verify(connections).moveToList(eq(CARD_ID), eq(ListType.DOING), eq(credentialFactory), any(TTCallback.class));

        todoFragment.onTaskMoved(0,ListType.DOING);
        assertFalse(todoFragment.list.contains(card));
        assertTrue(doingFragment.list.contains(card));

    }

    private TasksFragment getFragment(ListType type) {
        TasksFragment fragment = activity.fragments[type.ordinal()];
        fragment.listType = type;
        fragment.activity = activity;
        return fragment;
    }


}