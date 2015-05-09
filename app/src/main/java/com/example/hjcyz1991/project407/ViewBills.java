package com.example.hjcyz1991.project407;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hjcyz1991.project407.Model.Bill;
import com.example.hjcyz1991.project407.Model.User;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.util.ArrayList;
import java.util.List;


public class ViewBills extends ActionBarActivity {
    private Button iOwe;
    private Button oweMe;
    private ListView billList;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bills);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_view_bills)
                .setSwipeBackView(R.layout.swipeback_default);
        iOwe = (Button)findViewById(R.id.i_owe);
        oweMe = (Button)findViewById(R.id.owe_me);

        billList = (ListView)findViewById(R.id.bill_list_view);

        iOwe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOwe.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                oweMe.setBackgroundResource(android.R.drawable.btn_default);
                List<Bill> billPay = MainActivity.user.getBillPay();

            }
        });

        oweMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oweMe.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                iOwe.setBackgroundResource(android.R.drawable.btn_default);
                List<Bill> billRec = MainActivity.user.getBillRec();

            }
        });

        iOwe.performClick();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER );
        TextView actionbarTitle = (TextView)actionbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText("View Bills");
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
}
