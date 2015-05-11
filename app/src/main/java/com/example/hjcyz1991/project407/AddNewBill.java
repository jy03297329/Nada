package com.example.hjcyz1991.project407;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hjcyz1991.project407.Model.Backend;
import com.example.hjcyz1991.project407.Model.Bill;
import com.example.hjcyz1991.project407.Model.User;
import com.google.gson.JsonObject;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.util.ArrayList;
import java.util.List;


public class AddNewBill extends ActionBarActivity implements PaymentMethodDialog.Communicator {

    private User user;
    private String names;
    String[] namesArr;
    private Button addContact;
    private Button iAsk;
    private Button iPay;
    private EditText billName;
    private TextView totalAmtView;
    private EditText notes;
    private TextView peopleEdit;
    private final int REQUEST_ADD_GROUP = 0;
    private final int REQUEST_SET_AMOUNT = 2;
    private final int CONTACTS_SELECTED = 1;
    private final int AMOUNT_SET = 3;
    private ArrayList<Integer> selectedItemsID;
    private double[] amounts;
    private Button setAmounts;
    private boolean addedContact = false;
    private Bundle bundleFromAddGrp;
    private Bundle bundleFromSetAmt;
    private createBillEventTask mCreate;

    private Bolean canContinue;


    //private class BackendBill

    // Can be NO_NETWORK for OFFLINE, SANDBOX for TESTING and LIVE for PRODUCTION
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "credential from developer.paypal.com";
    // when testing in sandbox, this is likely the -facilitator email address.
    private static final String CONFIG_RECEIVER_EMAIL = "your@email.com";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        canContinue = new Bolean();
        setContentView(R.layout.activity_add_new_bill);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_add_new_bill)
                .setSwipeBackView(R.layout.swipeback_default);
        String userBackendID = SaveSharedPreference.getUserName(getApplicationContext());
        List<User> users = User.find(User.class, "backend_id = ?", userBackendID);
        user = users.get(0);
        peopleEdit = (TextView) findViewById(R.id.people_edit);
        addContact = (Button) findViewById(R.id.button_add_contact);
        billName = (EditText)findViewById(R.id.set_bill_name);
        totalAmtView = (TextView)findViewById(R.id.set_total_amount);
        setAmounts = (Button)findViewById(R.id.button_set_amounts);
        notes = (EditText)findViewById(R.id.set_notes);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewBill.this, AddGroups.class);
                if(addedContact){
                    intent.putExtra("contactsSelected", bundleFromAddGrp);
                    startActivityForResult(intent, REQUEST_ADD_GROUP);
                }else{
                    addedContact = true;
                    startActivityForResult(intent, REQUEST_ADD_GROUP);
                }
            }
        });

        setAmounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedItemsID == null){
                    Toast.makeText(AddNewBill.this, "Please select at least one friend",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(AddNewBill.this, SetAmounts.class);
                intent.putExtra("contactsSelected", bundleFromAddGrp);
                startActivityForResult(intent, REQUEST_SET_AMOUNT);
            }
        });


        iAsk = (Button)findViewById(R.id.i_ask);
        iPay = (Button)findViewById(R.id.i_pay);

        iAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creat new bill
                String billNameStr =  billName.getText().toString().trim();
                if(billNameStr.length() == 0){
                    billName.setError("Bill Name Cannot Be Empty");
                    billName.requestFocus();
                    return;
                }
                if(notes.getText().toString().trim().length() == 0){
                    notes.setError("Notes cannot be empty");
                    notes.requestFocus();
                    return;
                }
                if(namesArr.length == 0 || namesArr == null){
                    peopleEdit.requestFocus();
                    return;
                }

                List<Bill> bills = new ArrayList<Bill>();
                String totalAmtStr = totalAmtView.getText().toString();
                double totalAmt = Double.parseDouble(totalAmtStr);
                String notesStr = notes.getText().toString();

                for(int i = 0; i < selectedItemsID.size(); i++) {
                    Bill newBill = new Bill();
                    //newBill.creditor_id = user.backendId;
                    newBill.debtor_id = selectedItemsID.get(i);
                    newBill.amount = amounts[i];
                    bills.add(newBill);
                }
                mCreate = new createBillEventTask(user, billNameStr, Double.toString(totalAmt), notesStr,
                        user.backendId, bills);
                mCreate.execute();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //while(!canContinue.check()){};

                System.out.print(billNameStr + " " + totalAmtStr + " " + notesStr);
        }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == CONTACTS_SELECTED && requestCode == REQUEST_ADD_GROUP) {
            if (data.hasExtra("contactsSelected")) {
                bundleFromAddGrp = data.getExtras().getBundle("contactsSelected");
//                Bundle bundleID = data.getExtras().getBundle("contactsSelectedID");
                namesArr = bundleFromAddGrp.getStringArray("selectedItems");
                //Selected users' ids for backend communications
                selectedItemsID = bundleFromAddGrp.getIntegerArrayList("selectedItemsID");
                names = namesArr[0];
                for(int i = 1; i < namesArr.length; i++){
                    names = names + ", " + namesArr[i];
                }
                peopleEdit.setText(names);
            }
        }


        if (resultCode == AMOUNT_SET && requestCode == REQUEST_SET_AMOUNT) {
            if (data.hasExtra("amountSet")) {
                bundleFromSetAmt = data.getExtras().getBundle("amountSet");
                amounts = bundleFromSetAmt.getDoubleArray("amounts");
                double totalAmt = 0;
                for(int i = 0; i < amounts.length; i ++){
                    Bill newBill = new Bill();
                    newBill.creditor_id = user.backendId;

                    totalAmt += amounts[i];
                }
                totalAmtView.setText(Double.toString(totalAmt));
            }

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
        actionbarTitle.setText("Add New Bill");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
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
    //I pay
    public void showDialog(View v){
        if(billName.getText().toString().trim().length() == 0){
            billName.setError("Bill Name Cannot Be Empty");
            billName.requestFocus();
        }
        if(namesArr == null || namesArr.length >1){
            peopleEdit.requestFocus();
            return;
        }
        if(notes.getText().toString().trim().length() == 0){
            notes.setError("Notes cannot be empty");
            notes.requestFocus();
            return;
        }
        if(selectedItemsID.size() > 0) Log.d(null, "more than one creditor!");
        int creditorId = selectedItemsID.get(0);
        List<User> users = User.find(User.class, "backend_id = ?", Integer.toString(creditorId));
        String receiver = users.get(0).name;
        FragmentManager fm = getFragmentManager();
        PaymentMethodDialog paymentDialog = new PaymentMethodDialog();
        paymentDialog.show(fm, "PaymentDialog");
        Bundle data = new Bundle();
        data.putString("receiver", receiver);
        paymentDialog.setArguments(data);

        double totalAmt = 0;
        List<Bill> bills = new ArrayList<Bill>();
        for(int i = 0; i < selectedItemsID.size(); i++) {
            Bill newBill = new Bill();
            //newBill.creditor_id = user.backendId;
            newBill.debtor_id = user.backendId;
            newBill.amount = amounts[i];
            totalAmt += amounts[i];
            bills.add(newBill);
        }
        Log.d(null, bills.toString());
        mCreate = new createBillEventTask(user, billName.getText().toString().trim(),
                Double.toString(totalAmt), notes.getText().toString().trim(),
                selectedItemsID.get(0), bills);
        mCreate.execute();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //while(!canContinue.check()){};

    }

    @Override
    public void onDialogMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public class createBillEventTask extends AsyncTask<Void, Void, Boolean>{
        private User curUser;
        private String eventName;
        private String total;
        private String note;
        private int creditorId;
        private List<Bill> billList;
        private Bolean flag;

        createBillEventTask(User u, String e, String t, String n, int c, List<Bill> b){
            flag = new Bolean();
            curUser = u;
            eventName = e;
            total = t;
            note = n;
            creditorId = c;
            billList = b;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final String TAG = "CREATE_BILL_TASK";
            Backend.addBillEvent(curUser, total, eventName, note, creditorId, billList,
                    new Backend.BackendCallback(){
                @Override
                public void onRequestCompleted(Object result) {
                    List<Bill> bills = (List<Bill>) result;
                    for(Bill i : bills){
                        Bill newBill = new Bill();
                        newBill.copy(i);
                        newBill.save();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Event Created.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    flag.release();
                    canContinue.release();
                }
                @Override
                public void onRequestFailed(final String message){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    flag.lock();
                    canContinue.release();
                }
            });
            while(!canContinue.check()){};
            return flag.check();
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                AddNewBill.this.finish();
            }
        }

    }
}
