package com.jonatantierno.trellotimer;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jonatan on 15/04/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class MainActivityTest {

    MainActivity activity;
    ActivityController<MainActivity> activityController;
    StatusStore store = mock(StatusStore.class);
    TTConnections connections = mock(TTConnections.class);
    CredentialFactory credentialFactory = mock(CredentialFactory.class);
    TTApplication application;

    @Before
    public void setup(){
        activityController = Robolectric.buildActivity(MainActivity.class);
        activity = activityController.get();

        application = (TTApplication) RuntimeEnvironment.application;

        application.store = store;
        application.credentialFactory= credentialFactory;
    }

    @Test
    public void whenAlreadySignedInThenGoToSelectLists(){
        when(store.userLoggedIn()).thenReturn(true);

        activityController.create();

        verify(credentialFactory).init(activity);
        verify(store).init(activity);
        
        checkGoneToConfig(activity);
    }

    private void checkGoneToConfig(MainActivity activity) {
        Intent nextActivity = Shadows.shadowOf(activity).getNextStartedActivity();

        assertNotNull(nextActivity);
        assertEquals(ConfigActivity.class.getCanonicalName(), nextActivity.getComponent().getClassName());
        assertTrue(activity.isFinishing());
    }

    @Test
    public void whenNotSignedThenAskForCredentialsThenStoreResult() throws IOException {
        when(store.userLoggedIn()).thenReturn(false);

        activityController.create();

        assertEquals(View.VISIBLE, activity.button.getVisibility());

        // Success after Logging in
        activity.onLogInSuccess();

        verify(store).checkLoggedIn();
        checkGoneToConfig(activity);
    }
}