package com.example.hjcyz1991.project407;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hjcyz1991.project407.Model.Bill;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.util.ArrayList;
import java.util.List;


public class SendReminder extends ActionBarActivity {
    private Button sendReminder;
    private Button cancel;
    private ListView payableView;
    private ArrayAdapter<String> payableAdapter;
    private int billID[];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_reminder);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_send_reminder)
                .setSwipeBackView(R.layout.swipeback_default);

        sendReminder = (Button)findViewById(R.id.button_send_reminder);
        cancel = (Button)findViewById(R.id.button_cancel);
        payableView = (ListView)findViewById(R.id.bill_list_view_reminder);



        List<Bill> payableList = MainActivity.user.getBillPay();
        String[] payableContent = new String[payableList.size()];
        for(int i = 0; i < payableList.size(); i++){
            billID[i] = payableList.get(i).backendId;
            payableContent[i] = payableList.get(i).toString();
        }
        payableAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, payableContent);

        sendReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean added = false;
                SparseBooleanArray checked = payableView.getCheckedItemPositions();
//                ArrayList<String> selectedItems = new ArrayList<String>();
                ArrayList<Integer> selectedItemsID = new ArrayList<Integer>();
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)){
                        added = true;
//                        selectedItems.add(payableAdapter.getItem(position));
                        selectedItemsID.add(billID[position]);
                    }
                }
                if(!added){
                    Toast.makeText(SendReminder.this, "Please select at least one bill",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                //selected bills' ids
                int[] outputArrID = new int[selectedItemsID.size()];
                for (int i = 0; i < selectedItemsID.size(); i++) {
                    outputArrID[i] = selectedItemsID.get(i);
                }
                Toast.makeText(SendReminder.this, "Reminders sent",
                        Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar_no_menu, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER );
        TextView actionbarTitle = (TextView)actionbar.findViewById(R.id.actionbar_title);

        actionbarTitle.setText("Send Reminder");
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
    @Override
    protected  void onStop() {
        super.onStop();
        finish();
    }
}
