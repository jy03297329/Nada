package com.example.hjcyz1991.project407;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hjcyz1991.project407.Model.Backend;
import com.example.hjcyz1991.project407.Model.User;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.util.List;


public class ChangePassword extends ActionBarActivity {
    private Button changePassword;
    private EditText currPasswordView;
    private EditText newPasswordView;
    private EditText confirmNewPasswordView;
    private User user;

    private class Bolean{
        public boolean flag;
        Bolean(){
            flag = false;
        }
        public void lock(){
            flag = false;
        }
        public void release(){
            flag = true;
        }
        public boolean check(){
            return flag;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_change_password)
                .setSwipeBackView(R.layout.swipeback_default);

        changePassword = (Button) findViewById(R.id.button_reset_password);
        currPasswordView = (EditText)findViewById(R.id.curr_password);
        newPasswordView = (EditText)findViewById(R.id.new_password);
        confirmNewPasswordView = (EditText)findViewById(R.id.new_password_confirm);
        String userBackendID = SaveSharedPreference.getUserName(getApplicationContext());
        Log.d("CHANGE_PASSWORD", "getting user ID from change_pwd activity: " + userBackendID);
        List<User> users = User.find(User.class, "backend_id = ?", userBackendID);
        user = users.get(0);
        Log.d(null, user.toString());

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cancel = false;
                View focusView = null;

                String currPassword = currPasswordView.getText().toString();
                String newPassword = newPasswordView.getText().toString();
                String newPasswordConfirm = confirmNewPasswordView.getText().toString();

                if (TextUtils.isEmpty(currPassword)) {
                    currPasswordView.setError(getString(R.string.error_field_required));
                    focusView = currPasswordView;
                    cancel = true;
                }

                else if (!currPassword.equals(user.authToken)) {
                    currPasswordView.setError("Incorrect Current Password");
                    focusView = currPasswordView;
                    cancel = true;
                }
                else if (TextUtils.isEmpty(newPassword)) {
                    newPasswordView.setError(getString(R.string.error_field_required));
                    focusView = newPasswordView;
                    cancel = true;
                }
                else if (!TextUtils.isEmpty(newPassword) && !isPasswordValid(newPassword)) {
                    newPasswordView.setError(getString(R.string.error_invalid_password));
                    focusView = newPasswordView;
                    cancel = true;
                }
                else if (TextUtils.isEmpty(newPasswordConfirm)) {
                    confirmNewPasswordView.setError(getString(R.string.error_field_required));
                    focusView = confirmNewPasswordView;
                    cancel = true;
                }
                else if (!TextUtils.isEmpty(newPasswordConfirm) && !isPasswordValid(newPasswordConfirm)) {
                    confirmNewPasswordView.setError(getString(R.string.error_invalid_password));
                    focusView = confirmNewPasswordView;
                    cancel = true;
                }
                else if (!newPassword.equals(newPasswordConfirm)) {
                    confirmNewPasswordView.setError("New Passwords Do Not Match");
                    focusView = confirmNewPasswordView;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    //update new password to backend;
                    new ChangePasswordTask(user, currPassword).execute();
                    finish();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER );
        TextView actionbarTitle = (TextView)actionbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText("Profile");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(actionbar, params);
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

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    public class ChangePasswordTask extends AsyncTask<Void, Void, Boolean>{

        private User curUser;
        private String newPW;
        final private Bolean f;
        ChangePasswordTask(User u, String newPassword){
            //Log.d("INTASKCLASS", "change pw for: " + curUser.toString());
            curUser = u;
            newPW = newPassword;
            f = new Bolean();
            f.lock();
        }

        @Override
        protected Boolean doInBackground(Void...params){
            final String TAG = "CHANGE_PASSWORD_TASK";

            Backend.changePassword(Integer.toString(curUser.backendId), curUser.name, curUser.authToken
                    , newPW, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(Object result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Password changed successfully.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    f.release();
                }

                @Override
                public void onRequestFailed(final String message) {
                    Log.d(TAG, "Received error from Backend: " + message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            return f.check();
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                finish();
            }
        }
    }
}
