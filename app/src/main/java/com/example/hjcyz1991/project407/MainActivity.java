package com.example.hjcyz1991.project407;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // UI references.
    private TextView IOwe;
    private TextView owedMe;
    private TextView balance;
    private TextView textViewBills;
    private Button addNewBill;
    private Button payBack;
    private Button sendReminder;
    private Button viewBills;
    private Button profile;
    private Button settings;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //Set title in actionbar to center by using custom view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER );
        TextView actionbarTitle = (TextView)actionbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText("Nada");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(actionbar, params);

        addNewBill = (Button) findViewById(R.id.add_new_bill);
        addNewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNewBill.class);
                startActivity(intent);
            }
        });

        payBack = (Button) findViewById(R.id.pay_back);
        payBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PayBack.class);
                startActivity(intent);
            }
        });

        sendReminder = (Button)findViewById(R.id.send_reminder);
        sendReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SendReminder.class);
                startActivity(intent);
            }
        });

        viewBills = (Button)findViewById(R.id.view_bills);
        viewBills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewBills.class);
                startActivity(intent);
            }
        });

        profile = (Button)findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
        });

        settings = (Button)findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_add_friends:
                Intent intentAddFriends = new Intent(this, AddFriends.class);
                startActivity(intentAddFriends);
                return true;
            case R.id.action_add_groups:
                Intent intentAddGroups = new Intent(this, AddGroups.class);
                startActivity(intentAddGroups);
                return true;
            case R.id.action_scan_ur_code:
                Intent intentScanURCode = new Intent(this, ScanURCode.class);
                startActivity(intentScanURCode);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*public class LoadFriendshipTask extends AsyncTask<Void, Void, Boolean>{


        @Override
        protected Boolean doInBackground(Void... params){

        }

    }*/

}

