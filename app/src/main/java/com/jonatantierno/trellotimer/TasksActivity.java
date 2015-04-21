package com.jonatantierno.trellotimer;

import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;


public class TasksActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    TasksFragment[] fragments = new TasksFragment[ListType.SIZE];
    boolean testing = false;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    CredentialFactory credentialFactory;
    StatusStore store;
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
        connections = ((TTApplication) getApplication()).connections;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_credentials) {
            credentialFactory.deleteCredentials();
            store.reset();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        if (id == R.id.action_configure_lists) {
            startActivity(new Intent(this,SelectBoardActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void addToList(ListType listType, Item movedTask) {
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
