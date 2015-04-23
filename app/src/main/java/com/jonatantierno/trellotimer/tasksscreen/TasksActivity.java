package com.jonatantierno.trellotimer.tasksscreen;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.jonatantierno.trellotimer.CredentialFactory;
import com.jonatantierno.trellotimer.MainActivity;
import com.jonatantierno.trellotimer.R;
import com.jonatantierno.trellotimer.StatusStore;
import com.jonatantierno.trellotimer.TTApplication;
import com.jonatantierno.trellotimer.configscreens.SelectBoardActivity;
import com.jonatantierno.trellotimer.db.TaskStore;
import com.jonatantierno.trellotimer.trellorequests.TTCallback;
import com.jonatantierno.trellotimer.trellorequests.TTConnections;
import com.jonatantierno.trellotimer.model.Item;
import com.jonatantierno.trellotimer.model.ListType;
import com.jonatantierno.trellotimer.model.Task;

import java.util.List;

/**
 * This activity shows the task of the three lists (TO DO, DOING, DONE), and allows to movement
 * between lists.
 */
public class TasksActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static final String EXTRA_CARD_ID = "EXTRA_CARD_ID";
    public static final String EXTRA_CARD_ID_TO_FINISH = "EXTRA_CARD_TO_FINISH";

    SectionsPagerAdapter mSectionsPagerAdapter;

    TasksFragment[] fragments = new TasksFragment[ListType.SIZE];
    boolean testing = false;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    CredentialFactory credentialFactory;
    StatusStore store;
    TaskStore taskStore;
    TTConnections connections;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        getGlobalObjects();

        initUxElements();
        initTabs();
    }

    private void initTabs() {
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if (!testing) {
            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);
                }
            });

            // For each of the sections in the app, add a tab to the action bar.
            for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                actionBar.addTab(
                        actionBar.newTab()
                                .setText(mSectionsPagerAdapter.getPageTitle(i))
                                .setTabListener(this));
            }
        }
    }

    private void initUxElements() {
        fragments[ListType.TODO.ordinal()] = TasksFragment.newInstance(ListType.TODO.ordinal());
        fragments[ListType.DOING.ordinal()] = TasksFragment.newInstance(ListType.DOING.ordinal());
        fragments[ListType.DONE.ordinal()] = TasksFragment.newInstance(ListType.DONE.ordinal());

        progressBar = (ProgressBar) findViewById(R.id.tasks_progressBar);
    }

    private void getGlobalObjects() {
        credentialFactory = ((TTApplication) getApplication()).credentialFactory;
        store = ((TTApplication) getApplication()).store;
        taskStore = ((TTApplication) getApplication()).taskStore;
        connections = ((TTApplication) getApplication()).connections;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final String cardToFinish = getIntent().getStringExtra(EXTRA_CARD_ID_TO_FINISH);
        if (cardToFinish != null){
            mViewPager.setCurrentItem(ListType.DONE.ordinal(),false);
            finishReceivedCard(cardToFinish);
        }
    }

    private void finishReceivedCard(final String cardToFinish) {
        startLoading();
        connections.moveToList(
                cardToFinish,
                ListType.DONE,
                credentialFactory,
                new TTCallback<List<Item>>() {

                    @Override
                    public void success(List<Item> result) {
                        Task task = taskStore.getTask(cardToFinish);
                        int position = fragments[ListType.DOING.ordinal()].list.indexOf(task);

                        onTaskMoved(position,ListType.DOING,ListType.DONE);
                        fragments[ListType.DONE.ordinal()].infoTextView.setText(R.string.task_moved_to_done);
                    }

                    @Override
                    public void failure(Throwable cause) {
                        Log.e("TAG", cause.toString());
                        fragments[ListType.DONE.ordinal()].infoTextView.setText(R.string.trello_error);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuOptionId = item.getItemId();
        if (menuOptionId == R.id.action_delete_data) {
            credentialFactory.deleteCredentials();
            store.reset();
            taskStore.clear();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        if (menuOptionId == R.id.action_configure_lists) {
            startActivity(new Intent(this, SelectBoardActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void addToList(ListType listType, Task movedTask) {
        fragments[listType.ordinal()].addToList(movedTask);
    }

    public boolean isLoading() {
        return progressBar.getVisibility() == View.VISIBLE;
    }

    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void stopLoading() {
        progressBar.setVisibility(View.GONE);
    }

    void onTaskMoved(int position, ListType fromType, ListType toType) {
        stopLoading();
        Task movedTask = fragments[fromType.ordinal()].list.remove(position);
        fragments[fromType.ordinal()].listAdapter.notifyItemRemoved(position);

        addToList(toType, movedTask);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int tabIndex) {
            return fragments[tabIndex];
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return ListType.SIZE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(ListType.ofIndex(position).getStringId());
        }
    }

}
