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
import android.widget.EditText;
import android.widget.TextView;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;


public class EditBill extends ActionBarActivity {
    private EditText billNameEdit;
    private EditText amountEdit;
    private EditText notesEdit;
    private Button saveChange;
    private Button cancel;
    private int billBackendId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bill);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_edit_bill)
                .setSwipeBackView(R.layout.swipeback_default);
        billNameEdit = (EditText)findViewById(R.id.bill_name_edit);
        amountEdit = (EditText) findViewById(R.id.amount_edit);
        notesEdit = (EditText)findViewById(R.id.notes_edit);
        saveChange = (Button)findViewById(R.id.button_save_edit_bill);
        cancel = (Button)findViewById(R.id.button_cancel_edit_bill);
        Bundle bundle = getIntent().getExtras();
        String billName = bundle.getString("name");
        double amount = bundle.getDouble("amount");
        String notes = bundle.getString("notes");
        billBackendId = bundle.getInt("backendId");


        billNameEdit.setText(billName);
        amountEdit.setText(Double.toString(amount));
        notesEdit.setText(notes);

        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = billNameEdit.getText().toString();
                double newAmount = Double.parseDouble(amountEdit.getText().toString());
                String newNotes = notesEdit.getText().toString();
//                billBackendId;
                //update backend
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
//        getMenuInflater().inflate(R.menu.menu_view_bills, menu);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER );
        TextView actionbarTitle = (TextView)actionbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText("Edit Bill");
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
