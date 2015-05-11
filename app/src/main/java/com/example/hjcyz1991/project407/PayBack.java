package com.example.hjcyz1991.project407;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
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

import com.example.hjcyz1991.project407.Model.Bill;
import com.example.hjcyz1991.project407.Model.User;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import org.w3c.dom.Text;

import java.util.List;


public class PayBack extends ActionBarActivity implements PaymentMethodDialog.Communicator{
//    private EditText payback_search;
//    private Button pay;
//    private TextView infoView;
    private TableLayout tableLayout;
    private User user;
    private List<Bill> billList;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_back);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_pay_back)
                .setSwipeBackView(R.layout.swipeback_default);
//        payback_search = (EditText)findViewById(R.id.pay_back_search);
//        pay = (Button)findViewById(R.id.button_pay);
//        infoView = (TextView)findViewById(R.id.pay_back_info);
        tableLayout = (TableLayout)findViewById(R.id.pay_back_row_container);
        billList = user.getBillPay();
        for(int i = 0; i < billList.size(); i ++){
            position = i;
            TableRow rowView = new TableRow(this);
            rowView.setLayoutParams(new TableRow.LayoutParams((TableRow.LayoutParams.MATCH_PARENT), TableRow.LayoutParams.WRAP_CONTENT));
            TextView textView = new TextView(this);
            textView.setLayoutParams(new TableRow.LayoutParams((TableRow.LayoutParams.WRAP_CONTENT), TableRow.LayoutParams.WRAP_CONTENT, 2f));
            textView.setText(billList.get(i).toString(user.backendId));
            Button payButton = new Button(this);
            payButton.setLayoutParams(new TableRow.LayoutParams((TableRow.LayoutParams.WRAP_CONTENT), TableRow.LayoutParams.WRAP_CONTENT, 1f));
            payButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(v);
                }
            });
            rowView.addView(textView,0);
            rowView.addView(payButton,1);
            tableLayout.addView(rowView, i);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER );

        TextView actionbarTitle = (TextView)actionbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText("Pay Back");
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
    public void showDialog(View v){
//        String receiver = billList.get(position).debtor_id;
        FragmentManager fm = getFragmentManager();
        PaymentMethodDialog paymentDialog = new PaymentMethodDialog();
        paymentDialog.show(fm, "PaymentDialog");
        Bundle data = new Bundle();
//        data.putString("receiver", receiver);
        paymentDialog.setArguments(data);
        paymentDialog.setArguments(data);
    }

    @Override
    public void onDialogMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}
