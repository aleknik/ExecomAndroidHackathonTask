package eu.execom.todolistgrouptwo.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.web.client.RestClientException;

import java.util.List;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.adapter.TaskAdapter;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.model.Task;
import eu.execom.todolistgrouptwo.model.dao.TaskDAO;
import eu.execom.todolistgrouptwo.preference.UserPreferences_;

/**
 * Home {@link AppCompatActivity Activity} for navigation and listing all tasks.
 */
@OptionsMenu(R.menu.home_menu)
@EActivity(R.layout.activity_home)
public class HomeActivity extends AppCompatActivity {

    /**
     * Used for logging purposes.
     */
    private static final String TAG = HomeActivity.class.getSimpleName();

    /**
     * Used for identifying results from different activities.
     */
    protected static final int ADD_TASK_REQUEST_CODE = 42;
    protected static final int LOGIN_REQUEST_CODE = 420;
    public static final int TASK_DETAILS_REQUEST_CODE = 33;

    /**
     * Tasks are kept in this list during a user session.
     */
    private List<Task> tasks;

    /**
     * {@link FloatingActionButton FloatingActionButton} for starting the
     * {@link AddTaskActivity AddTaskActivity}.
     */
    @ViewById
    FloatingActionButton addTask;

    /**
     * {@link ListView ListView} for displaying the tasks.
     */
    @ViewById
    ListView listView;

    /**
     * {@link TaskAdapter Adapter} for providing data to the {@link ListView listView}.
     */
    @Bean
    TaskAdapter adapter;


    @Bean
    TaskDAO taskDAO;

    @Pref
    UserPreferences_ userPreferences;

    @RestService
    RestApi restApi;

    @AfterViews
    @Background
    void checkUser() {
        if (!userPreferences.accessToken().exists()) {
            LoginActivity_.intent(this).startForResult(LOGIN_REQUEST_CODE);
            return;
        }

//        tasks = taskDAO.findByUser(user);
        try {
            tasks = taskDAO.getTasks();
        } catch (RestClientException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        initData();
    }

    /**
     * Loads tasks from the {@link android.content.SharedPreferences SharedPreferences}
     * and sets the adapter.
     */
    @UiThread
    void initData() {
        listView.setAdapter(adapter);
        adapter.setTasks(tasks);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    // button show
                    addTask.show();
                } else {
                    // button hide
                    addTask.hide();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * Called when the {@link FloatingActionButton FloatingActionButton} is clicked.
     */
    @Click
    void addTask() {
        Log.i(TAG, "Add task clicked!");
        AddTaskActivity_.intent(this).startForResult(ADD_TASK_REQUEST_CODE);
    }

    /**
     * Called when the {@link AddTaskActivity AddTaskActivity} finishes.
     *
     * @param resultCode Indicates whether the activity was successful.
     * @param task       The new task.
     */
    @OnActivityResult(ADD_TASK_REQUEST_CODE)
    @Background
    void onResult(int resultCode, @OnActivityResult.Extra String task) {
        if (resultCode == RESULT_OK) {
//            Toast.makeText(this, task, Toast.LENGTH_SHORT).show();
            final Gson gson = new Gson();
            final Task newTask = gson.fromJson(task, Task.class);

            try {
                final Task newNewTask = taskDAO.create(newTask);
                onTaskCreated(newNewTask);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    @UiThread
    void onTaskCreated(Task task) {
        tasks.add(task);
        adapter.addTask(task);
    }

    @OnActivityResult(LOGIN_REQUEST_CODE)
    void onLogin(int resultCode, @OnActivityResult.Extra("token") String token) {
        if (resultCode == RESULT_OK) {
            userPreferences.accessToken().put(token);
            checkUser();
        }
    }

    @OptionsItem(R.id.item_logout)
    void logout() {
        userPreferences.accessToken().remove();
        LoginActivity_.intent(this).startForResult(LOGIN_REQUEST_CODE);
    }

    @ItemClick
    void listViewItemClicked(Task task) {
        Log.i(TAG, "Task clicked!");
        Intent intent = new Intent(this, TaskDetailsActivity_.class);
        intent.putExtra("taskId", task.getId());
        startActivityForResult(intent, TASK_DETAILS_REQUEST_CODE);
    }

    @OnActivityResult(TASK_DETAILS_REQUEST_CODE)
    @Background
    void onTaskUpdate(int resultCode, @OnActivityResult.Extra String task,
                      @OnActivityResult.Extra boolean remove) {
        if (resultCode == RESULT_OK) {
            final Gson gson = new Gson();
            final Task newTask = gson.fromJson(task, Task.class);

            if (remove) {
                try {
                    final Task newNewTask = taskDAO.remove(newTask.getId());
                    onTaskRemoved(newNewTask);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            } else {

                try {
                    final Task newNewTask = taskDAO.update(newTask);
                    onTaskUpdated(newNewTask);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

        }
    }

    @UiThread
    void onTaskRemoved(Task newNewTask) {
        tasks.remove(newNewTask);
        adapter.setTasks(tasks);
    }

    @UiThread
    void onTaskUpdated(Task task) {
        for (Task t : tasks) {
            if (t.equals(task)) {
                t.setTitle(task.getTitle());
                t.setDescription(task.getDescription());
                t.setFinished(task.isFinished());

                adapter.updateTask(task);
                return;
            }
        }
    }
}
