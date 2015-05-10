package com.example.hjcyz1991.project407;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.hjcyz1991.project407.Model.Backend;
import com.example.hjcyz1991.project407.Model.*;
import com.google.gson.Gson;
import com.orm.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private class Lock {
        private boolean locker;

        Lock() {
            locker = false;
        }

        public boolean isLocked() {
            return locker;
        }

        public void release() {
            locker = false;
        }

        public void lock() {
            locker = true;
        }
    }

    private Bolean f = new Bolean();
    private UserLoginTask mAuthTask = null;
    private LoadUserFriendTask mFriendTask = null;
    private LoadUserPayBillTask mBillPayTask = null;
    private LoadUserRecBillTask mBillRecTask = null;
    private final User curUser = new User();
    private Lock loadLock = new Lock();
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button login;
    private Button register;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //If user has logged in is not signed out go to Main
        if (SaveSharedPreference.getUserName(this).length() != 0) {
            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra("userBackendID", SaveSharedPreference.getUserName(this));
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.login_email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        login = (Button) findViewById(R.id.button_login);
        //findViewById(R.id.)
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();

            }
        });

        register = (Button) findViewById(R.id.button_Register);
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Registration.class);
                Bundle bundle = new Bundle();
                bundle.putString("email", mEmailView.getText().toString());
                bundle.putString("password", mPasswordView.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            //hide keyboard
            InputMethodManager inputManager = (InputMethodManager)
                    this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(0, 0);
            Log.d(null, "try login");
            loadLock.lock();
            mAuthTask = new UserLoginTask(email, password);
            /*{
                @Override
                protected void onPostExecute(Boolean success) {
                    super.onPostExecute(success);
                }
            }*/
            //mAuthTask.onPostExecute();
            mAuthTask.execute((Void) null);
            //Log.d(null, "try login2");
            //while(curUser.backendId == 0){ if(!loadLock.isLocked()) return;};


        }
    }

    private boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        //private final User curUser;
        //private final ProgressDialog progressDialog;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            final String TAG = "LOGIN_ACTIVITY";
            final Context currContext = LoginActivity.this;

            //curUser = new User();
            //while(!isCancelled()){
            Backend.logIn(mEmail, mPassword, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(Object result) {
                    final User user = (User) result;
                    SaveSharedPreference.setUserName(getApplicationContext(), Integer.toString(user.backendId));
                    curUser.copy(user);
                    Log.d(TAG, "curUser: " + curUser.toString());
                    //Log.d(TAG, "Login success. User: " + user.toString());
                    Log.d(TAG, "Login success. curUser: " + curUser.toString());
                    //check db for user with existing backendId. If doesn't exit, then save
                    List<User> users = User.find(User.class, "backend_id = ?", new Integer(
                            user.backendId).toString());
                    //SaveSharedPreference prefs = PreferenceManager.getDefaultSharedPreferences(currContext);
                    //SharedPreferences.Editor editor = prefs.edit();

                    if (users.size() == 0) {
                        Log.d(null, "new user: " + user.toString());
                        user.save();
                        Log.d(null, "new user added no problem: " + user.backendId);
                        SaveSharedPreference.setUserName(currContext, Integer.toString(user.backendId));
                        //editor.putString("loggedInId", Integer.toString(user.backendId));
                        //editor.commit();
                        //curUser.copy(user);

                    } else {
                        final User tempUser = users.get(0);
                        if (tempUser.backendId != user.backendId) {
                            Log.d(null, "backendId somehow changed!");
                            tempUser.backendId = user.backendId;
                        }
                        //Log.d(null, "currUser backendId: " + tempUser.backendId);
                        tempUser.authToken = mPassword;
                        //curUser.authToken = mPassword;
                        tempUser.authTokenConfirm = mPassword;
                        //curUser.authTokenConfirm = mPassword;
                        //Log.d(null, "curr user: " + tempUser.toString());
                        //Log.d(null, "****" + tempUser.authToken.toString());

                        tempUser.save();
                        curUser.copy(tempUser);
                        SaveSharedPreference.setUserName(currContext, Integer.toString(user.backendId));
                        //editor.putString("loggedInId", Long.toString(tempUser.getId()));
                        //editor.commit();

                    }
                    loadLock.release();
                    //}
                    //});
                }

                @Override
                public void onRequestFailed(final String message) {
                    //mFriendTask.cancel(true);
                    //mBillTask.cancel(true);
                    //NOTE: parameter validation and filtering is handled by the backend, just show the
                    //returned error message to the user
                    Log.d(TAG, "Received error from Backend: " + message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    loadLock.release();
                    //return false;

                }
            });
            Log.d(TAG, "curUser bot: " + curUser.toString());
            while (loadLock.isLocked()) {};
            return (curUser.backendId != 0);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //Log.d(null, "2");
            mAuthTask = null;
            showProgress(false);
            if (success) {
                mFriendTask = new LoadUserFriendTask();
                mFriendTask.execute((Void) null);
                loadLock.lock();
                f.lock();
                mBillPayTask = new LoadUserPayBillTask();
                mBillPayTask.execute();
            } else {

               // mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();

            }
        }

        @Override
        protected void onCancelled() {
            Log.d(null, "on cancel is called");
            mAuthTask = null;
            showProgress(false);
        }
    }

    //one more class to load the bill
    public class LoadUserFriendTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            //while(!isCancelled()){
            final String TAG = "LOGIN_ACTIVITY_LF";
            final Context currContext = LoginActivity.this;
            //while(loadLock.isLocked()){if(isCancelled()) return null;};

            Backend.loadUserFriends(curUser, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(Object result) {
                    final List<User> resultFriends = (List<User>) result;
                    Log.d(TAG, "FriendList get success. " + resultFriends.size() + " friends");
                    //List<User> users = User.find(User.class, "backend_id = ?", new Integer(
                      //      curUser.backendId).toString());
                    List<User> allUsers = User.listAll(User.class);
                    List<Integer> allUsersId = new ArrayList<Integer>();
                    for(User i : allUsers)
                        allUsersId.add(i.backendId);
                    Log.d(TAG, "old user size: " + allUsers.size());

                    for(User i : resultFriends){
                        if(!allUsersId.contains(i.backendId)){
                            User newUser = new User();
                            newUser.copy(i);
                            newUser.save();
                            Log.d(TAG, "new user added: " + newUser.toString());
                        }
                    }

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
            //while(loadLock.isLocked()){};
            return true;//}
            //lockFriend = false;
            //return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(true);

            if (success) {

            } else {
            }
        }

        @Override
        protected void onCancelled() {
            Log.d(null, "friend on cancel is called");
            //running = false;
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class LoadUserPayBillTask extends AsyncTask<Void, Void, Boolean> {

        LoadUserPayBillTask() {
            Log.d(null, "created a new billTask");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //while(!isCancelled()){
            final String TAG = "LOGIN_ACTIVITY_LB";
            Log.d(TAG, "billTask in");
            Backend.loadUserPayBill(curUser, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(Object result) {
                    f.release();
                    final List<Bill> resultBills = (List<Bill>) result;
                    Log.d(TAG, "BillList get success. User: " + curUser.toString());
                    for (Bill i : resultBills) {
                        if (!curUser.getAllBillId().contains(i.backendId)) {
                            Bill newBill = new Bill();
                            newBill.copy(i);
                            newBill.save();
                            Log.d(TAG, "new bill saved: " + newBill.toString());
                        }
                    }
                    loadLock.release();
                    //Log.d(TAG, "move on to main");
                }

                @Override
                public void onRequestFailed(final String message) {
                    f.lock();
                    Log.d(TAG, "Received error from Backend: " + message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    loadLock.release();

                }
            });
            while(loadLock.isLocked()){};
            return f.check();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(true);

            if (success) {
                f.lock();
                loadLock.lock();
                mBillRecTask = new LoadUserRecBillTask();
                mBillRecTask.execute();
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            Log.d(null, "bill on cancel is called");
            mAuthTask = null;
            showProgress(false);
        }
    }
    public class LoadUserRecBillTask extends AsyncTask<Void, Void, Boolean> {

        LoadUserRecBillTask() {
            Log.d(null, "created a new billTask");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //while(!isCancelled()){
            final String TAG = "LOGIN_ACTIVITY_LB";
            Log.d(TAG, "billRecTask in");
            Backend.loadUserRecBill(curUser, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(Object result) {
                    //Log.d(null, "5");
                    f.release();
                    final List<Bill> resultBills = (List<Bill>) result;
                    final List<Bill> allBills = Bill.find(Bill.class,
                            StringUtil.toSQLName("creditor_id") + " = ?",
                            Integer.toString(curUser.backendId));
                    Log.d(TAG, "*****" + allBills.toString());
                    final List<Integer> allBillsId = new ArrayList<Integer>();
                    for(Bill i : allBills)
                        allBillsId.add(i.backendId);
                    Log.d(TAG, "BillList get success. User: " + curUser.toString());
                    for (Bill i : resultBills) {
                        if (!allBillsId.contains(i.backendId)) {
                            Bill newBill = new Bill();
                            newBill.copy(i);
                            newBill.save();
                            Log.d(TAG, "new bill saved: " + newBill.toString());
                        }
                    }
                    loadLock.release();
                    Log.d(TAG, "move on to main");
                }

                @Override
                public void onRequestFailed(final String message) {
                    f.lock();
                    Log.d(TAG, "Received error from Backend: " + message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    loadLock.release();
                }
            });
            while(loadLock.isLocked()){};
            return f.check();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(true);

            if (success) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            Log.d(null, "bill on cancel is called");
            mAuthTask = null;
            showProgress(false);
        }
    }
}



