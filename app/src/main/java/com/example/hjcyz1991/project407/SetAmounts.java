package com.example.hjcyz1991.project407;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.util.ArrayList;


public class SetAmounts extends ActionBarActivity {
    private Bundle bdlFrAddBill;
    private TableLayout tableLayout;
    private final int AMOUNT_SET = 3;
    private Button setAmount;
    private Button cancel;
    String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_amounts);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_set_amounts)
                .setSwipeBackView(R.layout.swipeback_default);
        tableLayout = (TableLayout)findViewById(R.id.amounts_table_container);
        setAmount = (Button)findViewById(R.id.button_finish_set_amt);
        cancel = (Button)findViewById(R.id.button_cancel_set_amt);
        bdlFrAddBill = this.getIntent().getBundleExtra("contactsSelected");
        names = bdlFrAddBill.getStringArray("selectedItems");
        ArrayList<Integer> debtorIds = bdlFrAddBill.getIntegerArrayList("selectedItemsID");
        for(int i = 0; i < names.length; i ++){
            TableRow rowView = new TableRow(this);
            rowView.setLayoutParams(new TableRow.LayoutParams((TableRow.LayoutParams.MATCH_PARENT), TableRow.LayoutParams.WRAP_CONTENT));
            TextView textView = new TextView(this);
            textView.setLayoutParams(new TableRow.LayoutParams((TableRow.LayoutParams.WRAP_CONTENT), TableRow.LayoutParams.WRAP_CONTENT, 2f));
            textView.setText(names[i] + " $: ");
            EditText editText = new EditText(this);
            editText.setLayoutParams(new TableRow.LayoutParams((TableRow.LayoutParams.WRAP_CONTENT), TableRow.LayoutParams.WRAP_CONTENT, 1f));
            editText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText.setMaxLines(1);
            editText.setSingleLine();
            rowView.addView(textView,0);
            rowView.addView(editText,1);
            tableLayout.addView(rowView, i);
        }

        setAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double[] amounts = new double[tableLayout.getChildCount()];
                for(int i = 0; i < amounts.length; i ++){
                    View view = tableLayout.getChildAt(i);
                    if(view instanceof TableRow){
                        TableRow row = (TableRow)view;
                        View childView = row.getChildAt(1);
                        if(childView instanceof EditText) {
                            EditText editText = (EditText) childView;
                            if(editText.getText().toString().trim().length() == 0){
                                editText.setError("Amount cannot be empty");
                                editText.requestFocus();
                                return;
                            }
                            amounts[i] = Double.parseDouble(editText.getText().toString());
                            if(amounts[i] < 0 ) {
                                editText.setError("Amount cannot be negative");
                                editText.requestFocus();
                            }
                        }
                    }
                }

                bdlFrAddBill.putDoubleArray("amounts", amounts);
                Intent intent = new Intent();
                intent.putExtra("amountSet", bdlFrAddBill);
                setResult(AMOUNT_SET, intent);
                finish();
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
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_set_amounts, menu);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER );
        TextView actionbarTitle = (TextView)actionbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText("Set Individual Amount");
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
