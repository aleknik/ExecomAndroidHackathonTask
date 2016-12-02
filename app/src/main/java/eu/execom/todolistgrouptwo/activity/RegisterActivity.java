package eu.execom.todolistgrouptwo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.web.client.RestClientException;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.model.User;
import eu.execom.todolistgrouptwo.model.dto.RegisterDTO;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {

    @ViewById
    EditText name;

    @ViewById
    EditText username;

    @ViewById
    EditText password;

    @RestService
    RestApi restApi;

    @EditorAction(R.id.password)
    @Click
    void register() {
        final String name = this.name.getText().toString();
        final String username = this.username.getText().toString();
        final String password = this.password.getText().toString();
        final User user = new User(name, username, password);

        registerUser(user);
    }

    @Background
    void registerUser(User user) {
        boolean succesfulRegister;
        try {
            final RegisterDTO registerDTO = new RegisterDTO(user.getUsername(), user.getPassword(), user.getPassword());
            restApi.register(registerDTO);
            succesfulRegister = true;
        } catch (RestClientException e) {
            succesfulRegister = false;
        }


        if (succesfulRegister) {
            final Intent intent = new Intent();
            intent.putExtra("username", user.getUsername());
            intent.putExtra("password", user.getPassword());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            showRegisterError();
        }
    }

    @UiThread
    void login(User user) {
        final Intent intent = new Intent();
        intent.putExtra("user_id", user.getId());

        setResult(RESULT_OK, intent);
        finish();
    }

    @UiThread
    void showRegisterError() {
        username.setError("Username already exists.");
    }

}
