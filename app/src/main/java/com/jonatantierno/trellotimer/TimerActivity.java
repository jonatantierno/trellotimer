package com.jonatantierno.trellotimer;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DateFormat;


public class TimerActivity extends ActionBarActivity {


    TextView taskNameTextView;
    TextView pomodorosTextView;
    TextView secondsSpentTextView;
    TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        taskNameTextView = (TextView) findViewById(R.id.taskNameTextView);
        pomodorosTextView= (TextView) findViewById(R.id.pomodorosTextView);
        secondsSpentTextView = (TextView) findViewById(R.id.secondsSpentTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String curentTaskId = getIntent().getStringExtra(TasksActivity.EXTRA_CARD_ID);

        Task currentTask = ((TTApplication)getApplication()).taskStore.getTask(curentTaskId);

        taskNameTextView.setText(currentTask.name);
        pomodorosTextView.setText(currentTask.pomodoros + getString(R.string.pomodoros));
        secondsSpentTextView.setText(format(currentTask.secondsSpent));

    }

    private String format(long secondsSpent) {
        long hours= secondsSpent/3600;
        long minutes = (secondsSpent - hours*3600)/60;
        long seconds = (secondsSpent - hours*3600 - minutes*60);

        StringBuilder b = new StringBuilder();
        b.append(hours).append("h ").append(minutes).append("m ").append(seconds).append('s');

        return b.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void showTime(String time) {
        timeTextView.setText(time);
    }
}
