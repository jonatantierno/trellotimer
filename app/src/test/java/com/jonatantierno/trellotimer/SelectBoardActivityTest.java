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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by jonatan on 15/04/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class SelectBoardActivityTest {

    public static final String BOARD_NAME = "board_name";
    public static final String BOARD_ID = "board_id";
    SelectBoardActivity activity;
    ActivityController<SelectBoardActivity> activityController;
    TTConnections connections = mock(TTConnections.class);
    StatusStore store = mock(StatusStore.class);
    CredentialFactory credentialFactory = mock(CredentialFactory.class);

    TTApplication application;
    @Before
    public void setup(){
        activityController = Robolectric.buildActivity(SelectBoardActivity.class);
        activity = activityController.get();

        application = (TTApplication) RuntimeEnvironment.application;

        application.credentialFactory= credentialFactory;
        application.connections = connections;
        application.store = store;

    }

    @Test
    public void shouldCallBoards(){
        activityController.create();

        verify(connections).getBoards(credentialFactory, activity);
    }


    @Test
    public void whenBoardSelectedShouldStoreIt(){
        activityController.create();

        Item board = new Item("board_id", "board_name");
        activity.success(Collections.singletonList(board));

        assertEquals(View.GONE, activity.progressBar.getVisibility());

        activity.onItemSelected(0);

        verify(store).saveBoard(board);

        checkAdvanceToConfig(activity);
    }

    private void checkAdvanceToConfig(SelectBoardActivity activity) {
        Intent nextActivity = Shadows.shadowOf(activity).getNextStartedActivity();

        assertNotNull(nextActivity);
        assertEquals(SelectListsActivity.class.getCanonicalName(), nextActivity.getComponent().getClassName());
        assertFalse(activity.isFinishing());
    }
}