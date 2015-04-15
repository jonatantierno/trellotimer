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

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jonatan on 15/04/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class ConfigActivityTest {

    ConfigActivity activity;
    ActivityController<ConfigActivity> activityController;
    TTConnections connections = mock(TTConnections.class);
    CredentialFactory credentialFactory = mock(CredentialFactory.class);

    TTApplication application;
    @Before
    public void setup(){
        activityController = Robolectric.buildActivity(ConfigActivity.class);
        activity = activityController.get();

        application = (TTApplication) RuntimeEnvironment.application;

        application.credentialFactory= credentialFactory;
        application.connections = connections;
    }

    @Test
    public void shouldCallBoards(){
        activityController.create();

        verify(connections).getBoards(credentialFactory, activity);
    }

}