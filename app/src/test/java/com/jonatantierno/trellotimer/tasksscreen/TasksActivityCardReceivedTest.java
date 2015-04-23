package com.jonatantierno.trellotimer.tasksscreen;

import android.content.Intent;
import android.view.View;

import com.jonatantierno.trellotimer.BuildConfig;
import com.jonatantierno.trellotimer.CredentialFactory;
import com.jonatantierno.trellotimer.StatusStore;
import com.jonatantierno.trellotimer.TTApplication;
import com.jonatantierno.trellotimer.db.TaskStore;
import com.jonatantierno.trellotimer.model.ListType;
import com.jonatantierno.trellotimer.trellorequests.TTCallback;
import com.jonatantierno.trellotimer.trellorequests.TTConnections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
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

/**
 * Created by jonatan on 15/04/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class TasksActivityCardReceivedTest {

    public static final String CARD_ID = "card_id_2";

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
    public void whenReceivedCardToFinishThenMoveItAndGoToDone(){
        final Intent intent = new Intent().putExtra(TasksActivity.EXTRA_CARD_ID_TO_FINISH, CARD_ID);

        activityController.withIntent(intent).create().resume();

        assertEquals(View.VISIBLE, activity.progressBar.getVisibility());
        verify(connections, times(1)).moveToList(eq(CARD_ID), eq(ListType.DONE), eq(credentialFactory), any(TTCallback.class));

        assertEquals(ListType.DONE.ordinal(),activity.mViewPager.getCurrentItem());
    }
}