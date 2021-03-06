package com.example.hjcyz1991.project407;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
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

import com.example.hjcyz1991.project407.Model.Backend;
import com.example.hjcyz1991.project407.Model.Bill;
import com.example.hjcyz1991.project407.Model.BillEvent;
import com.example.hjcyz1991.project407.Model.User;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.orm.StringUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;


public class PayBack extends ActionBarActivity implements PaymentMethodDialog.Communicator{
//    private EditText payback_search;
//    private Button pay;
//    private TextView infoView;
    private TableLayout tableLayout;
    private User user;
    private List<Bill> billList;
    private int position;
    private SettleTask mSettle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay_back);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_pay_back)
                .setSwipeBackView(R.layout.swipeback_default);
        String userBackendID = SaveSharedPreference.getUserName(getApplicationContext());
        //Log.d("MAIN_ACTIVITY", "getting user ID from login activity: " + userBackendID);
        List<User> users = User.find(User.class, "backend_id = ?", userBackendID);
        user = users.get(0);

//        payback_search = (EditText)findViewById(R.id.pay_back_search);
//        pay = (Button)findViewById(R.id.button_pay);
//        infoView = (TextView)findViewById(R.id.pay_back_info);
        tableLayout = (TableLayout)findViewById(R.id.pay_back_row_container);

        //try{
            //billList.clear();
            billList = Bill.find(Bill.class, StringUtil.toSQLName("debtor_id") + " = ?",
                    Integer.toString(user.backendId));
        for(int i = billList.size() - 1; i > -1; i--){
            if(billList.get(i).settled)
                billList.remove(i);
        }
        //}catch (ConcurrentModificationException a){
           // try {
                //Thread.sleep(3000);
                //billList = user.getBillPay();
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}
        //}
        Log.d(null, "bill size: " + billList.size());

        for(int i = 0; i < billList.size(); i ++){
            position = i;
            TableRow rowView = new TableRow(this);
            rowView.setLayoutParams(new TableRow.LayoutParams((TableRow.LayoutParams.MATCH_PARENT), TableRow.LayoutParams.WRAP_CONTENT));
            TextView textView = new TextView(this);
            textView.setLayoutParams(new TableRow.LayoutParams((TableRow.LayoutParams.WRAP_CONTENT), TableRow.LayoutParams.WRAP_CONTENT, 2f));
            textView.setText(billList.get(i).toString(user.backendId));
            textView.setTextAppearance(this, android.R.style.TextAppearance_Large);
            Button payButton = new Button(this);
            payButton.setLayoutParams(new TableRow.LayoutParams((TableRow.LayoutParams.WRAP_CONTENT), TableRow.LayoutParams.WRAP_CONTENT, 1f));
            final String BID = Integer.toString(billList.get(i).backendId);
            payButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(v);
                    mSettle = new SettleTask(BID);
                    mSettle.execute();
                }
            });
            payButton.setText("Pay Back");
            rowView.addView(textView,0);
            rowView.addView(payButton,1);
            tableLayout.addView(rowView, i);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar_no_menu, null);
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
    @Override
    protected  void onStop() {
        super.onStop();
        finish();
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        this.onCreate(null);
//    }
    public class SettleTask extends AsyncTask<Void, Void, Boolean> {

        private String billId;
        SettleTask(String b) {
            billId = b;
            Log.d(null, "created a new EventTask");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //while(!isCancelled()){
            final String TAG = "LOGIN_ACTIVITY_EVENT";
            Log.d(TAG, "EventTask in");
            Backend.settleBill(Integer.toString(user.backendId), user.authToken, billId,
                    new Backend.BackendCallback() {
                        @Override
                        public void onRequestCompleted(Object result) {
                            Log.d(null, "5");

                            Bill setBill = (Bill)result;
                            int bId = setBill.backendId;
                            List<Bill> bills = Bill.find(Bill.class, "backend_id = ?", Integer.toString(bId));
                            bills.get(0).delete();

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
            return true;
        }
    }
}
