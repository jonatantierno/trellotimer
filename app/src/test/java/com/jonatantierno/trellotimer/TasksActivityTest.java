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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
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

        TasksFragment fragment = activity.fragments[ListType.TODO.ordinal()];
        fragment.listType = ListType.TODO;
        fragment.activity = activity;

        final View selectedView = mock(View.class);

        Item card= new Item(CARD_ID, CARD_NAME);

        fragment.list.add(card);
        fragment.onItemSelected(0, selectedView);

        verify(connections).moveToList(eq(CARD_ID), eq(ListType.DOING), eq(credentialFactory), any(TTCallback.class));

        //assertFalse(fragment.list.contains(card));
    }


}