package com.example.hjcyz1991.project407;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hjcyz1991.project407.Model.Backend;
import com.example.hjcyz1991.project407.Model.Friendship;
import com.example.hjcyz1991.project407.Model.User;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddFriends extends ActionBarActivity {

    EditText searchBox;
    View focusView = null;
    Boolean cancel = false;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    EditText search;

    //private final User curUser = new User();


    private User user;
    private User friend = new User();
    private SearchFriendTask mSearchFriendTask = null;
    private Lock searchLock = new Lock();

    private class Lock{
        private boolean locker;
        Lock(){ locker = false;}
        public boolean isLocked(){ return locker;}
        public void release(){  locker = false;}
        public void lock(){ locker = true;}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        String userBackendID = SaveSharedPreference.getUserName(getApplicationContext());
        List<User> users = User.find(User.class, "backend_id = ?", userBackendID);
        user = users.get(0);
        List<User> curUserFriends = user.getFriends();
        final List<String> friendEmails = new ArrayList<String>();
        for(User i : curUserFriends){
            friendEmails.add(i.email);
        }
        Log.d("friendsEmail", friendEmails.size() + " friends");
        searchBox = (EditText) findViewById(R.id.search);
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                Log.d("null", "in");

                if (id == R.id.search_friends || id == EditorInfo.IME_NULL) {
                    if(keyEvent.getAction() == KeyEvent.ACTION_UP)
                        return false;
                    //Get search value
                    Log.d("null", "out");
                    String searchVal = searchBox.getText().toString();
                    //Validate input
                    if (TextUtils.isEmpty(searchVal)) {
                        searchBox.setError(getString(R.string.error_field_required));
                        focusView = searchBox;
                        cancel = true;
                    } else if (!isEmailValid(searchVal)) {
                        searchBox.setError(getString(R.string.error_invalid_email));
                        focusView = searchBox;
                        cancel = true;
                    }
                    if (cancel) {
                        focusView.requestFocus();
                    }
                    searchLock.lock();
                    Log.d(null, "search again?");
                    if(friendEmails.contains(searchVal)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "You are already friends.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return false;
                    }
                    mSearchFriendTask = new SearchFriendTask(searchVal);
                    mSearchFriendTask.execute();
                    while(searchLock.isLocked()){};
                    new AddFriendTask(searchVal).execute();
                    System.out.println(searchVal);
                    return true;
                }
                return false;
            }
        });
        //get value user typed in the search box
        String searchVal = searchBox.getText().toString();
        System.out.println(searchVal);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        TextView actionbarTitle = (TextView) actionbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText("Add Friends");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
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

    private boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public class SearchFriendTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;

        SearchFriendTask(String email) {
            mEmail = email;
            searchLock.lock();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final String TAG = "SEARCH_TASK";

            Backend.searchUser(mEmail, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(Object result) {
                    //curUser.copy((User) result);
                    //store the friend name
                    friend.copy((User) result);
                    searchLock.release();
                    Log.d(TAG, "friendId: " + Integer.toString(friend.backendId));
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
            if(searchLock.isLocked())
                Log.d(TAG, "locked");
            return (!searchLock.isLocked());
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.d("SEARCH_TASK", "search user succeeded");
                //while (searchLock.isLocked()) {};
            }else{

            }
        }
    }

    public class AddFriendTask extends AsyncTask<Void, Void, Boolean>{

        private String mEmail;

        AddFriendTask(String email){
            mEmail = email;
            Log.d(null, "add firend: " + email);
            //searchLock.lock();
        }

        @Override
        protected Boolean doInBackground(Void... params){
            final String TAG = "ADD_FRIEND_TASK";

            Backend.addFriend(user.backendId, user.authToken, mEmail, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(Object result) {
                    //int friendId = (int)result;
                    /*List<User> resultUser = User.find(User.class, "backend_id = ?", new Integer(
                            friend.backendId).toString());
                    User newUser;
                    if (resultUser.size() > 1) Log.d(TAG, "duplicate users: " + resultUser.size());
                    if (resultUser.size() == 0) {
                        newUser = new User();
                        newUser.backendId = friend.backendId;
                        newUser.save();
                    } else {
                        newUser = resultUser.get(0);
                    }*/

                    //Friendship record1 = new Friendship(user, newUser);
                    //record1.save();
                    //Log.d(TAG, "saving : " + record1.toString());
                    //new Friendship(newUser, user).save();

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
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            finish();
        }

    }


}
