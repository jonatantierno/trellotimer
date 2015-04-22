package com.jonatantierno.trellotimer;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TimerActivity extends ActionBarActivity {
    public static final int THIRTY_MINUTES = 30 * 60 * 1000;
    public static final int MS_PER_H = 3600 * 1000;
    public static final int MS_PER_MIN = (60 * 1000);


    TextView taskNameTextView;
    TextView pomodorosTextView;
    TextView secondsSpentTextView;
    TextView timeTextView;
    Button pomodoroButton;
    Button pauseButton;
    View startClockLayout;
    View controlClockLayout;

    TTClock clock = new TTClock(this);
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        taskNameTextView = (TextView) findViewById(R.id.taskNameTextView);
        pomodorosTextView= (TextView) findViewById(R.id.pomodorosTextView);
        secondsSpentTextView = (TextView) findViewById(R.id.secondsSpentTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        pomodoroButton =(Button) findViewById(R.id.pomodoroButton);
        pauseButton =(Button) findViewById(R.id.pauseButton);
        controlClockLayout =findViewById(R.id.controlClockLayout);
        startClockLayout =findViewById(R.id.startClockLayout);

        pomodoroButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startClockLayout.setVisibility(View.GONE);
                controlClockLayout.setVisibility(View.VISIBLE);

                clock.start(THIRTY_MINUTES,System.currentTimeMillis());
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clock.isPaused()) {
                    pauseClock();
                } else {
                    clock.unpause(System.currentTimeMillis());
                    pauseButton.setText(R.string.pause);

                }
            }
        });
    }

    private void pauseClock() {
        pauseButton.setText(R.string.continue_button);

        clock.pause(System.currentTimeMillis());

        final Task updatedTask = currentTask.increaseTaskTime(clock.getElapsedTime());
        ((TTApplication)getApplication()).taskStore.updateTask(updatedTask);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String curentTaskId = getIntent().getStringExtra(TasksActivity.EXTRA_CARD_ID);

        currentTask = ((TTApplication)getApplication()).taskStore.getTask(curentTaskId);

        taskNameTextView.setText(currentTask.name);
        pomodorosTextView.setText(currentTask.pomodoros + getString(R.string.pomodoros));
        secondsSpentTextView.setText(format(currentTask.timeSpent));

    }

    @Override
    protected void onPause() {
        super.onPause();

        pauseClock();
    }

    static String format(long timeSpent) {
        long hours= timeSpent/ MS_PER_H;
        long minutes = (timeSpent - hours*MS_PER_H)/ MS_PER_MIN;
        long seconds = (timeSpent - hours*MS_PER_H - minutes* MS_PER_MIN)/1000;

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

    void timeUp() {
        startClockLayout.setVisibility(View.VISIBLE);
        controlClockLayout.setVisibility(View.GONE);

        final Task updatedTask = currentTask.increaseTaskPomodoros(clock.getElapsedTime());

        ((TTApplication)getApplication()).taskStore.updateTask(updatedTask);
    }
}
