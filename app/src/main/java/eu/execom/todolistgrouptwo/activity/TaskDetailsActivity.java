package eu.execom.todolistgrouptwo.activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.model.Task;

@EActivity(R.layout.activity_task_details)
public class TaskDetailsActivity extends AppCompatActivity {

    @ViewById
    Button remove;

    @ViewById
    Button ok;

    @ViewById
    CheckBox completed;

    @ViewById
    Button cancel;

    @ViewById
    TextInputEditText title;

    @ViewById
    TextInputEditText description;

    @RestService
    RestApi restApi;

    private Long id;

    @AfterViews
    @Background
    void GetTask() {
        Intent intent = getIntent();
        Long taskId = intent.getLongExtra("task_id", 0);
        Task task = restApi.getTask(taskId);
        id = taskId;

        initData(task);


    }

    @UiThread
    void initData(Task task) {
        title.setText(task.getTitle());
        description.setText(task.getDescription());
        completed.setChecked(task.isFinished());
    }


    @Click
    void ok() {
        generateResult(false);
    }

    @Background
    void generateResult(boolean remove) {
        final Task task = new Task(title.getText().toString(),
                description.getText().toString());
        task.setFinished(completed.isChecked());
        task.setId(id);
        final Intent intent = new Intent();
        final Gson gson = new Gson();
        intent.putExtra("task", gson.toJson(task));

        if (remove)
            intent.putExtra("remove", true);
        else
            intent.putExtra("remove", false);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Click
    void cancel() {
        finish();
    }

    @Click
    void remove() {
        generateResult(true);
    }


}
