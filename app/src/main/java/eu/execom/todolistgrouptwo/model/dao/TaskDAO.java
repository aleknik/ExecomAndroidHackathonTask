package eu.execom.todolistgrouptwo.model.dao;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.model.Task;
import eu.execom.todolistgrouptwo.model.User;

@EBean
public class TaskDAO {

    @RestService
    RestApi restApi;

    public Task create(Task task) {
//        taskDAO.create(task);
        return restApi.createTask(task);
    }

    public Task update(Task task) {
        return restApi.updateTask(task, task.getId());
    }

    public Task remove(Long id) {
        return restApi.removeTask(id);
    }

}
