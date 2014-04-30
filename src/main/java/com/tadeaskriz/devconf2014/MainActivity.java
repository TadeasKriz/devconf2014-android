package com.tadeaskriz.devconf2014;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.Registrations;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends Activity implements MessageHandler {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_EDIT = REQUEST_ADD + 1;

    private short retries = 10;

    @ViewById
    LinearLayout unfinishedTasks;

    @ViewById
    LinearLayout finishedTasks;

    @ViewById
    TextView unfinishedTasksTitle;

    @ViewById
    TextView finishedTasksTitle;

    @App
    BaseApplication application;

    Pipeline mainPipeline;

    Pipe<Task> tasksPipe;

    @AfterViews
    void init() {
        registerPush();

        URL url;
        try {
            url = new URL("http://devconf2014-detox.rhcloud.com/rest/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        mainPipeline = new Pipeline(url);

        PipeConfig config = new PipeConfig(url, Task.class);
        config.setEndpoint("tasks");

        tasksPipe = mainPipeline.pipe(Task.class, config);

        reloadTasks();
    }

    private void reloadTasks() {
        tasksPipe.read(new Callback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                success(tasks);
            }

            @Override
            public void onFailure(Exception e) {
                failure(e);
            }
        });
    }

    @UiThread
    void registerPush() {
        application.getRegistrar().register(getApplicationContext(), new Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
                Log.d(TAG, "Successfully registered for push notifications.");
            }

            @Override
            public void onFailure(Exception e) {
                Log.w(TAG, "Registration for push notifications failed!", e);
                if (retries == 0) {
                    throw new RuntimeException(e);
                }
                retries--;
                registerPush();
            }
        });
    }

    @UiThread
    void success(List<Task> tasks) {
        unfinishedTasks.removeAllViews();
        finishedTasks.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

        for (final Task task : tasks) {
            LinearLayout targetLayout;

            if (task.isDone()) {
                targetLayout = finishedTasks;
            } else {
                targetLayout = unfinishedTasks;
            }

            final View taskItem = inflater.inflate(R.layout.task_item, targetLayout, false);
            if (taskItem == null) {
                Log.e(TAG, "Could not inflate task item xml!");
                continue;
            }

            taskItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditTaskActivity_.intent(MainActivity.this).task(task).startForResult(REQUEST_EDIT);
                }
            });
            String prefix = targetLayout == finishedTasks ? "finished" : "unfinished";
            taskItem.setContentDescription(prefix + "-item-" + targetLayout.getChildCount());
            CheckBox done = (CheckBox) taskItem.findViewById(R.id.done);
            TextView text = (TextView) taskItem.findViewById(R.id.text);

            done.setChecked(task.isDone());
            done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    task.setDone(isChecked);
                    tasksPipe.save(task, new Callback<Task>() {
                        @Override
                        public void onSuccess(Task data) { }

                        @Override
                        public void onFailure(Exception e) {
                            failure(e);
                        }
                    });
                }
            });

            text.setText(task.getText());

            targetLayout.addView(taskItem);
        }

        unfinishedTasksTitle.setVisibility(unfinishedTasks.getChildCount() == 0 ? View.GONE : View.VISIBLE);
        finishedTasksTitle.setVisibility(finishedTasks.getChildCount() == 0 ? View.GONE : View.VISIBLE);
    }

    @OptionsItem(R.id.action_add)
    void addNewTask() {
        AddTaskActivity_.intent(this).startForResult(REQUEST_ADD);
    }

    @OnActivityResult(REQUEST_ADD)
    void onAddNewTaskResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String text = data.getStringExtra(AddTaskActivity.RESULT_BUNDLE_TEXT);

            Task task = new Task();
            task.setText(text);

            tasksPipe.save(task, new Callback<Task>() {
                @Override
                public void onSuccess(Task task) {
                }

                @Override
                public void onFailure(Exception e) {
                }
            });
        }
    }

    @OnActivityResult(REQUEST_EDIT)
    void onEditTaskResult(int resultCode, Intent data) {
        if (resultCode == EditTaskActivity.RESULT_SAVE) {
            Task task = data.getParcelableExtra(EditTaskActivity.RESULT_BUNDLE_TASK);
            tasksPipe.save(task, new Callback<Task>() {
                @Override
                public void onSuccess(Task data) {
                }

                @Override
                public void onFailure(Exception e) {
                    failure(e);
                }
            });
        } else if (resultCode == EditTaskActivity.RESULT_DELETE) {
            Task task = data.getParcelableExtra(EditTaskActivity.RESULT_BUNDLE_TASK);
            tasksPipe.remove(String.valueOf(task.getId()), new Callback<Void>() {
                @Override
                public void onSuccess(Void data) {
                }

                @Override
                public void onFailure(Exception e) {
                    failure(e);
                }
            });
        }
    }

    @UiThread
    void failure(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Registrations.registerMainThreadHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Registrations.unregisterMainThreadHandler(this);
    }

    @Override
    public void onMessage(Context context, Bundle message) {
        reloadTasks();
    }

    @Override
    public void onDeleteMessage(Context context, Bundle message) {

    }

    @Override
    public void onError() {

    }
}
