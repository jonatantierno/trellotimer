package com.jonatantierno.trellotimer.configscreens;

import android.view.View;

import com.jonatantierno.trellotimer.BuildConfig;
import com.jonatantierno.trellotimer.CredentialFactory;
import com.jonatantierno.trellotimer.MainActivityTest;
import com.jonatantierno.trellotimer.R;
import com.jonatantierno.trellotimer.StatusStore;
import com.jonatantierno.trellotimer.TTApplication;
import com.jonatantierno.trellotimer.trellorequests.TTConnections;
import com.jonatantierno.trellotimer.model.Item;
import com.jonatantierno.trellotimer.model.ListType;
import com.jonatantierno.trellotimer.tasksscreen.TasksActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

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
public class SelectListsActivityTest {

    public static final String LIST_NAME_TODO = "list_name_todo";
    public static final String LIST_NAME_DOING = "list_name_doing";
    public static final String LIST_NAME_DONE = "list_name_done";
    private static final String LIST_NAME_TODO_AGAIN = "list_name_todo_again";
    SelectListsActivity activity;
    ActivityController<SelectListsActivity> activityController;
    TTConnections connections = mock(TTConnections.class);
    StatusStore store = mock(StatusStore.class);
    CredentialFactory credentialFactory = mock(CredentialFactory.class);

    TTApplication application;
    @Before
    public void setup(){
        activityController = Robolectric.buildActivity(SelectListsActivity.class);
        activity = activityController.get();

        application = (TTApplication) RuntimeEnvironment.application;

        application.credentialFactory= credentialFactory;
        application.connections = connections;
        application.store = store;

    }

    @Test
    public void shouldCallListsAndSelectTodo(){
        activityController.create();

        verify(connections).getLists(credentialFactory, activity);

        assertEquals(activity.getResources().getColor(R.color.text_current), activity.textViews[ListType.TODO.ordinal()].getCurrentTextColor());
        assertEquals(activity.getResources().getColor(R.color.text_pending), activity.textViews[ListType.DOING.ordinal()].getCurrentTextColor());
        assertEquals(activity.getResources().getColor(R.color.text_pending), activity.textViews[ListType.DONE.ordinal()].getCurrentTextColor());

    }

    @Test
    public void whenListsSelectedShouldStoreItAndSelectNext(){
        activityController.create();

        Item itemTodo = new Item("list_id_todo", LIST_NAME_TODO);
        Item itemDoing = new Item("list_id_doing", LIST_NAME_DOING);
        Item itemDone = new Item("list_id_done", LIST_NAME_DONE);
        Item itemTodoAgain = new Item("list_id_todoAgain", LIST_NAME_TODO_AGAIN);

        List<Item> list = new ArrayList<>();
        list.add(itemTodo);
        list.add(itemDoing);
        list.add(itemDone);
        list.add(itemTodoAgain);

        activity.success(list);

        assertEquals(View.GONE, activity.progressBar.getVisibility());

        View selectedItem = mock(View.class);
        activity.onItemSelected(0, selectedItem);

        verify(store).saveList(ListType.TODO, itemTodo);
        assertEquals(activity.getResources().getColor(R.color.text_correct), activity.textViews[ListType.TODO.ordinal()].getCurrentTextColor());
        assertEquals(activity.getResources().getColor(R.color.text_current), activity.textViews[ListType.DOING.ordinal()].getCurrentTextColor());
        assertEquals(activity.getResources().getColor(R.color.text_pending), activity.textViews[ListType.DONE.ordinal()].getCurrentTextColor());
        assertEquals(LIST_NAME_TODO, activity.editTexts[ListType.TODO.ordinal()].getText().toString());
        assertTrue(itemTodo.selected);

        activity.onItemSelected(1, selectedItem);

        verify(store).saveList(ListType.DOING, itemDoing);
        assertEquals(activity.getResources().getColor(R.color.text_correct), activity.textViews[ListType.TODO.ordinal()].getCurrentTextColor());
        assertEquals(activity.getResources().getColor(R.color.text_correct), activity.textViews[ListType.DOING.ordinal()].getCurrentTextColor());
        assertEquals(activity.getResources().getColor(R.color.text_current), activity.textViews[ListType.DONE.ordinal()].getCurrentTextColor());
        assertEquals(LIST_NAME_DOING, activity.editTexts[ListType.DOING.ordinal()].getText().toString());
        assertTrue(itemDoing.selected);

        activity.onItemSelected(2, selectedItem);

        verify(store).saveList(ListType.DONE, itemDone);
        assertEquals(activity.getResources().getColor(R.color.text_current), activity.textViews[ListType.TODO.ordinal()].getCurrentTextColor());
        assertEquals(activity.getResources().getColor(R.color.text_correct), activity.textViews[ListType.DOING.ordinal()].getCurrentTextColor());
        assertEquals(activity.getResources().getColor(R.color.text_correct), activity.textViews[ListType.DONE.ordinal()].getCurrentTextColor());
        assertEquals(LIST_NAME_DONE, activity.editTexts[ListType.DONE.ordinal()].getText().toString());
        assertTrue(itemDone.selected);

        assertEquals(View.VISIBLE, activity.listFinishButton.getVisibility());

        assertFalse(activity.isFinishing());

        activity.onItemSelected(3, selectedItem);

        assertFalse(itemTodo.selected);
        assertTrue(itemTodoAgain.selected);
    }

    @Test
    public void whenPressButtonThenGoToTasksScreen(){
        activityController.create();

        activity.listFinishButton.performClick();

        MainActivityTest.checkGoneTo(activity, TasksActivity.class);
    }

    @Test
    public void whenPressEditTextThenMakeCurrent(){
        activityController.create();

        activity.layouts[ListType.DONE.ordinal()].performClick();

        assertEquals(activity.getResources().getColor(R.color.text_pending), activity.textViews[ListType.TODO.ordinal()].getCurrentTextColor());
        assertEquals(activity.getResources().getColor(R.color.text_current), activity.textViews[ListType.DONE.ordinal()].getCurrentTextColor());
        activity.selectedListType = ListType.DONE;

    }

    @Test
    public void whenBackThenGoToSelectBoard(){
        activityController.create();

        activity.onBackPressed();

        MainActivityTest.checkGoneTo(activity, SelectBoardActivity.class);
    }
}