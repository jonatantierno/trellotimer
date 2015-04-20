package com.jonatantierno.trellotimer;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.google.api.client.auth.oauth2.Credential;

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
    public void setup() throws IOException {
        activityController = Robolectric.buildActivity(MainActivity.class);
        activity = activityController.get();

        application = (TTApplication) RuntimeEnvironment.application;

        application.store = store;
        application.credentialFactory= credentialFactory;
        when(credentialFactory.getCredential()).thenReturn(mock(Credential.class));
    }

    @Test
    public void whenAlreadySignedInThenGoToSelectBoard(){
        when(store.userLoggedIn()).thenReturn(true);

        activityController.create();

        verify(credentialFactory).init(activity);
        verify(store).init(activity);
        
        checkGoneTo(activity, SelectBoardActivity.class);
    }

    @Test
    public void whenAlreadyConfiguredThenGoToTaskActivity(){
        when(store.userFinishedConfig()).thenReturn(true);

        activityController.create();

        verify(credentialFactory).init(activity);
        verify(store).init(activity);

        checkGoneTo(activity, TasksActivity.class);
    }


    public static void checkGoneTo(Activity origin, Class destination) {
        Intent nextActivity = Shadows.shadowOf(origin).getNextStartedActivity();

        assertNotNull(nextActivity);
        assertEquals(destination.getCanonicalName(), nextActivity.getComponent().getClassName());
        assertTrue(origin.isFinishing());
    }

    @Test
    public void whenNotSignedThenAskForCredentialsThenStoreResult() throws IOException {
        when(store.userLoggedIn()).thenReturn(false);

        activityController.create();

        assertEquals(View.VISIBLE, activity.button.getVisibility());

        // Success after Logging in
        activity.onLogInSuccess();

        verify(store).checkLoggedIn();
        checkGoneTo(activity, SelectBoardActivity.class);
    }


    @Test
    public void whenErrorConnectingThenShowText(){
        when(store.userLoggedIn()).thenReturn(false);

        activityController.create();

        activity.onLogInSuccess();


        activity.button.performClick();

        assertEquals(View.GONE, activity.button.getVisibility());
        assertEquals(activity.getString(R.string.connecting), activity.textView.getText().toString());
    }

    @Test
    public void whenPressConnectThenHideButtonAndChangeText(){
        when(store.userLoggedIn()).thenReturn(false);

        activityController.create();

        activity.button.performClick();

        assertEquals(View.GONE, activity.button.getVisibility());
        assertEquals(activity.getString(R.string.connecting), activity.textView.getText().toString());
    }
}