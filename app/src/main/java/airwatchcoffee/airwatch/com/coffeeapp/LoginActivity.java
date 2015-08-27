package airwatchcoffee.airwatch.com.coffeeapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by josephben on 8/18/2015.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    public static final String LOGGED_IN="logged_in";
    public static final String TRUE="true";
    public static final String USERNAME="username";

    private EditText mUserName;
    private EditText mPassword;
    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasLoginDetails()) {
            finish();
            Log.v(TAG, "hasLoginDetails() returned true");
            startActivity(new Intent(this, MainActivity.class));
        } else {
            setContentView(R.layout.activity_login);

            mUserName = (EditText) findViewById(R.id.username_et);
            mPassword = (EditText) findViewById(R.id.password_et);
            mLogin = (Button) findViewById(R.id.login_button);

            mLogin.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              performLogin();
                                          }
                                      }
            );
        }

    }

    private boolean hasLoginDetails() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString(LOGGED_IN, "").equals(TRUE)) {
            return true;
        }
        return false;
    }

    private void saveLoginDetails() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LOGGED_IN, TRUE);
        editor.putString(USERNAME, mUserName.getText().toString());
        editor.apply(); //
    }

    // TODO: think hard before deciding on the access modifier.
    private void performLogin() {
        if (validateLogin()) {
            saveLoginDetails();
            Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            alertUser();
        }
    }

    private boolean validateLogin() {
        if (TextUtils.isEmpty(mUserName.getText())) {
            return false;
        }
        if (TextUtils.isEmpty(mPassword.getText())) {
            return false;
        }
        return true;
    }

    private void alertUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert)
                .setMessage(R.string.login_incorrect)
                .setPositiveButton(R.string.ok, null);
        builder.create().show();
    }
}
