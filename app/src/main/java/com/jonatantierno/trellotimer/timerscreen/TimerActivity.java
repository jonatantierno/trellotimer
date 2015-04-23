package com.jonatantierno.trellotimer.timerscreen;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jonatantierno.trellotimer.MainActivity;
import com.jonatantierno.trellotimer.R;
import com.jonatantierno.trellotimer.TTApplication;
import com.jonatantierno.trellotimer.configscreens.SelectBoardActivity;
import com.jonatantierno.trellotimer.trellorequests.TTCallback;
import com.jonatantierno.trellotimer.trellorequests.TTConnections;
import com.jonatantierno.trellotimer.tasksscreen.TasksActivity;
import com.jonatantierno.trellotimer.Formatter;
import com.jonatantierno.trellotimer.db.TaskStore;
import com.jonatantierno.trellotimer.model.Item;
import com.jonatantierno.trellotimer.model.Task;

import java.util.List;


public class TimerActivity extends ActionBarActivity {
    public static final long POMODORO_DURATION = 30 * 60 * 1000;
    public static final long LONG_BREAK_DURATION = 15 * 60 * 1000;
    public static final long SHORT_BREAK_DURATION = 5 * 60 * 1000;

    public static final long POMODORO_DURATION_DEBUG = 10*1000;
    public static final long LONG_BREAK_DURATION_DEBUG = 5 * 1000;
    public static final long SHORT_BREAK_DURATION_DEBUG = 3 * 1000;

    private long pomodoroDuration = POMODORO_DURATION;
    private long shortBreakDuration = SHORT_BREAK_DURATION;
    private long longBreakDuration = LONG_BREAK_DURATION;

    TextView taskNameTextView;
    TextView pomodorosTextView;
    TextView timeSpentTextView;
    TextView clockTextView;
    Button pomodoroButton;
    Button longBreakButton;
    Button shortBreakButton;
    Button pauseButton;
    Button doneButton;
    Button stopButton;

    View startClockLayout;
    View controlClockLayout;

    boolean inABreak=false;

    TTCountdown clock = new TTCountdown(this);
    TTSoundPlayer player = new TTSoundPlayer();

    private String currentId;
    private TaskStore taskStore;
    private TTConnections connections;


    private TTCallback<List<Item>> commentCallback = new TTCallback<List<Item>>() {
        @Override
        public void success(List<Item> result) {
            // Do nothing
        }

        @Override
        public void failure(Throwable cause) {
            Log.e("TAG",cause.toString());
            Toast.makeText(TimerActivity.this, getString(R.string.trello_error), Toast.LENGTH_SHORT).show();
        }
    };;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        taskStore = ((TTApplication) getApplication()).taskStore;
        connections = ((TTApplication) getApplication()).connections;

        findUxElements();


    }

    private void startPomodoro() {
        inABreak = false;
        showClockControls();
        showTime(Formatter.formatClock(pomodoroDuration));
        showTaskData(getCurrentTask());

        clock.start(pomodoroDuration, System.currentTimeMillis());
    }

    private void showClockControls() {
        startClockLayout.setVisibility(View.GONE);
        doneButton.setVisibility(View.GONE);
        controlClockLayout.setVisibility(View.VISIBLE);
    }

    private void startBreak(long duration) {
        inABreak = true;

        showClockControls();
        showTime(Formatter.formatClock(duration));

        taskNameTextView.setText(R.string.break_text);
        pomodorosTextView.setText("");
        timeSpentTextView.setText("");

        clock.start(duration, System.currentTimeMillis());
    }

    private void backTofinishTask() {
        final Intent intent = new Intent(TimerActivity.this, TasksActivity.class);
        intent.putExtra(TasksActivity.EXTRA_CARD_ID_TO_FINISH, currentId);
        startActivity(intent);
        finish();
    }

    private void findUxElements() {
        taskNameTextView = (TextView) findViewById(R.id.taskNameTextView);
        pomodorosTextView= (TextView) findViewById(R.id.pomodorosTextView);
        timeSpentTextView = (TextView) findViewById(R.id.secondsSpentTextView);
        clockTextView = (TextView) findViewById(R.id.timeTextView);
        pomodoroButton =(Button) findViewById(R.id.pomodoroButton);
        longBreakButton=(Button) findViewById(R.id.longBreakButton);
        shortBreakButton =(Button) findViewById(R.id.shortBreakButton);
        pauseButton =(Button) findViewById(R.id.pauseButton);
        stopButton =(Button) findViewById(R.id.stopButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        controlClockLayout =findViewById(R.id.controlClockLayout);
        startClockLayout =findViewById(R.id.startClockLayout);
    }

    private void pauseClock() {
        pauseButton.setText(R.string.continue_button);

        clock.pause(System.currentTimeMillis());

        if (!inABreak) {
            final Task updatedTask = getCurrentTask().increaseTaskTime(clock.getElapsedTime());
            taskStore.updateTask(updatedTask);

            showTaskData(updatedTask);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        currentId = getIntent().getStringExtra(TasksActivity.EXTRA_CARD_ID);

        showTaskData(getCurrentTask());
    }

    private void showTaskData(Task task) {
        taskNameTextView.setText(task.name);
        pomodorosTextView.setText(task.pomodoros + getString(R.string.pomodoros));
        timeSpentTextView.setText(Formatter.formatTimeSpent(task.timeSpent));
    }

    private Task getCurrentTask() {
        return taskStore.getTask(currentId);
    }

    @Override
    protected void onPause() {
        super.onPause();

        pauseClock();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuOptionId = item.getItemId();
        if (menuOptionId == R.id.action_delete_data) {
            ((TTApplication)getApplication()).credentialFactory.deleteCredentials();
            ((TTApplication)getApplication()).store.reset();
            ((TTApplication)getApplication()).taskStore.clear();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        if (menuOptionId == R.id.action_configure_lists) {
            startActivity(new Intent(this, SelectBoardActivity.class));
            finish();
            return true;
        }
        if (item.getItemId()==R.id.action_debug_times){
            switchDurationsForDebug();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchDurationsForDebug() {
        if (pomodoroDuration == POMODORO_DURATION){
            pomodoroDuration = POMODORO_DURATION_DEBUG;
            longBreakDuration = LONG_BREAK_DURATION_DEBUG;
            shortBreakDuration = SHORT_BREAK_DURATION_DEBUG;
        } else {
            pomodoroDuration = POMODORO_DURATION;
            longBreakDuration = LONG_BREAK_DURATION;
            shortBreakDuration = SHORT_BREAK_DURATION;
        }
    }

    void showTime(String time) {
        clockTextView.setText(time);
    }

    void timeUp() {
        startClockLayout.setVisibility(View.VISIBLE);
        controlClockLayout.setVisibility(View.GONE);
        doneButton.setVisibility(View.VISIBLE);

        player.play(this);

        final Task updatedTask = getCurrentTask().increaseTaskPomodoros(clock.getElapsedTime());

        taskStore.updateTask(updatedTask);

        showTaskData(updatedTask);
    }

    public void onClickStartPomodoro(View v) {
        startPomodoro();
    }
    public void onClickStartLongBreak(View v) {
        startBreak(longBreakDuration);
    }
    public void onClickStartShortBreak(View v) {
        startBreak(shortBreakDuration);
    }
    public void onClickPause(View v){
        if (!clock.isPaused()) {
            pauseClock();
        } else {
            clock.unpause(System.currentTimeMillis());
            pauseButton.setText(R.string.pause);

        }
    }
    public void onClickDone(View v){
        doneButton.setVisibility(View.GONE);

        Task currentTask = getCurrentTask();
        connections.addComment(currentTask.id, Formatter.getComment(this, currentTask), ((TTApplication) getApplication()).credentialFactory, commentCallback);

        backTofinishTask();
    }
    public void onClickStop(View v){
        inABreak = false;
        pauseClock();

        controlClockLayout.setVisibility(View.GONE);
        startClockLayout.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.GONE);
        pauseButton.setText(R.string.pause);
    }
}

