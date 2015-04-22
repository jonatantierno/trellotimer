package com.jonatantierno.trellotimer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jonatantierno.trellotimer.db.TaskStore;

import java.util.List;


public class TimerActivity extends ActionBarActivity {
    public static final int POMODORO_DURATION = 10*1000;//30 * 60 * 1000;
    public static final int MS_PER_H = 3600 * 1000;
    public static final int MS_PER_MIN = (60 * 1000);


    TextView taskNameTextView;
    TextView pomodorosTextView;
    TextView timeSpentTextView;
    TextView timeTextView;
    Button pomodoroButton;
    Button pauseButton;
    Button doneButton;
    View startClockLayout;
    View controlClockLayout;

    TTClock clock = new TTClock(this);
    TTSoundPlayer player = new TTSoundPlayer();

    private String currentId;
    private TaskStore taskStore;
    private TTConnections connections;


    private TTCallback<List<Item>> callback = new TTCallback<List<Item>>() {
        @Override
        public void success(List<Item> result) {

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

        pomodoroButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startClockLayout.setVisibility(View.GONE);
                doneButton.setVisibility(View.GONE);
                controlClockLayout.setVisibility(View.VISIBLE);
                showTime(TTClock.format(POMODORO_DURATION));

                clock.start(POMODORO_DURATION,System.currentTimeMillis());
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
        doneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                doneButton.setVisibility(View.GONE);

                Task currentTask = getCurrentTask();

                connections.addComment(currentTask.id, getComment(currentTask), ((TTApplication) getApplication()).credentialFactory, callback);

                backTofinishTask();
            }
        });
    }

    private void backTofinishTask() {
        final Intent intent = new Intent(TimerActivity.this, TasksActivity.class);
        intent.putExtra(TasksActivity.EXTRA_CARD_ID_TO_FINISH,currentId);
        startActivity(intent);
        finish();
    }

    private String getComment(Task task) {
        return new StringBuilder()
                .append(task.pomodoros)
                .append(getString(R.string.pomodoros))
                .append(", ")
                .append(format(task.timeSpent))
                .append(getString(R.string.total_spent))
                .toString();
    }

    private void findUxElements() {
        taskNameTextView = (TextView) findViewById(R.id.taskNameTextView);
        pomodorosTextView= (TextView) findViewById(R.id.pomodorosTextView);
        timeSpentTextView = (TextView) findViewById(R.id.secondsSpentTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        pomodoroButton =(Button) findViewById(R.id.pomodoroButton);
        pauseButton =(Button) findViewById(R.id.pauseButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        controlClockLayout =findViewById(R.id.controlClockLayout);
        startClockLayout =findViewById(R.id.startClockLayout);
    }

    private void pauseClock() {
        pauseButton.setText(R.string.continue_button);

        clock.pause(System.currentTimeMillis());

        final Task updatedTask = getCurrentTask().increaseTaskTime(clock.getElapsedTime());
        taskStore.updateTask(updatedTask);

        showTaskData(updatedTask);
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
        timeSpentTextView.setText(format(task.timeSpent));
    }

    private Task getCurrentTask() {
        return taskStore.getTask(currentId);
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
        doneButton.setVisibility(View.VISIBLE);

        player.play(this);

        final Task updatedTask = getCurrentTask().increaseTaskPomodoros(clock.getElapsedTime());

        taskStore.updateTask(updatedTask);

        showTaskData(updatedTask);
    }
}

class TTSoundPlayer{
    public void play(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer.create(context, R.raw.airhorn).start();
            }
        }).start();
    }
}
