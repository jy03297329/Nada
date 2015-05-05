package com.example.hjcyz1991.project407;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hjcyz1991.project407.Model.Bill;
import com.example.hjcyz1991.project407.Model.User;
import com.google.gson.Gson;

import java.io.IOError;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    public User user; // UI references.
    private TextView IOwe;
    private TextView owedMe;
    private TextView balance;
    private ListView listViewBills;
    private Button addNewBill;
    private Button payBack;
    private Button sendReminder;
    private Button viewBills;
    private Button profile;
    private Button settings;
    private final int LOGGED_OUT = 1;
//    private Bill[] bills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get user
        String userBackendID = SaveSharedPreference.getUserName(this);
        List<User> users = User.find(User.class, "backend_id = ?", userBackendID);
        user = users.get(0);

        listViewBills = (ListView) findViewById(R.id.list_view_bills);

        IOwe = (TextView)findViewById(R.id.I_owe);
        IOwe.setText("Payable: $" + Double.toString(user.moneyPay));

        owedMe = (TextView) findViewById(R.id.owed_me);
        owedMe.setText("Receivable: $" + Double.toString(user.moneyRec));

        balance = (TextView) findViewById(R.id.balance);
        balance.setText("Balance: $" + Double.toString(0 - user.moneyPay));

        String[] items = new String[] { "Vegetables","Fruits","Flower Buds","Legumes","Bulbs","Tubers" };
        ArrayAdapter<String> ListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        listViewBills.setAdapter(ListAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Log.d(null, Integer.toString(getIntent().getIntExtra("currentUser", 0)));
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
                startActivityForResult(intent, LOGGED_OUT);
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
                Intent intentScanURCode = new Intent(this, ScanQRCode.class);
                startActivity(intentScanURCode);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == LOGGED_OUT){
            finish();
        }
    }

}

