package com.jonatantierno.trellotimer.db;

import android.app.Activity;

import com.jonatantierno.trellotimer.BuildConfig;
import com.jonatantierno.trellotimer.MainActivity;
import com.jonatantierno.trellotimer.TTApplication;
import com.jonatantierno.trellotimer.Task;
import com.jonatantierno.trellotimer.TimerActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

/**
 * Created by jonatan on 21/04/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class TaskStoreTest {

    public static final String ID = "id";

    @Test
    public void shouldStoreAndRecoverTask(){
        TaskStore store = new TaskStore(RuntimeEnvironment.application);

        Task task = new Task(ID,"name",3,7);
        store.putTask(task);
        Task retrievedTask = store.getTask(ID);

        assertEquals(task,retrievedTask);
    }
}