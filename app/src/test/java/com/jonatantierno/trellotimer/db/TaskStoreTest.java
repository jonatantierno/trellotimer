package com.jonatantierno.trellotimer.db;

import com.jonatantierno.trellotimer.BuildConfig;
import com.jonatantierno.trellotimer.model.Task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by jonatan on 21/04/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class TaskStoreTest {

    public static final String ID = "id";
    public static final String ID_2 = "ID2";
    public static final String ID_3 = "ID3";

    @Test
    public void shouldStoreAndRecoverTask(){
        TaskStore store = new TaskStore(RuntimeEnvironment.application);

        Task task = new Task(ID,"name",3,7);
        store.putTask(task);
        Task retrievedTask = store.getTask(ID);

        assertEquals(task,retrievedTask);
    }

    @Test
    public void shouldUpdateAndRecoverTask(){
        TaskStore store = new TaskStore(RuntimeEnvironment.application);

        store.putTask(new Task(ID,"name",3,7));

        Task updatedTask = new Task(ID,"name",4,70);
        store.updateTask(updatedTask);

        Task retrievedTask = store.getTask(ID);

        assertEquals(updatedTask,retrievedTask);
    }

    @Test
    public void shouldClearData(){
        TaskStore store = new TaskStore(RuntimeEnvironment.application);

        store.putTask(new Task(ID,"name1",3,7));
        store.putTask(new Task(ID_2,"name2",3,7));
        store.putTask(new Task(ID_3,"name3",3,7));

        store.clear();

        assertNull(store.getTask(ID));
        assertNull(store.getTask(ID_2));
        assertNull(store.getTask(ID_3));
    }
}