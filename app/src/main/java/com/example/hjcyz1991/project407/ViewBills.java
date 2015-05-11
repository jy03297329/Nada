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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hjcyz1991.project407.Model.Backend;
import com.example.hjcyz1991.project407.Model.Bill;
import com.example.hjcyz1991.project407.Model.BillEvent;
import com.example.hjcyz1991.project407.Model.User;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.orm.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ViewBills extends ActionBarActivity {
    private Button iOwe;
    private Button oweMe;
    private ListView billView;
    private User user;
    private ArrayAdapter<String> arrayAdapterBillPay;
    private ArrayAdapter<String> arrayAdapterBillRec;
    private String[] billPayCtn;
    private String[] billRecCtn;
    private boolean payClicked;
    private BillEvent billEvent;
    private removeBillTask mRemoveBillTask;
    private LoadUserPayBillTask mLoadPayBill;
    private LoadUserRecBillTask mLoadRecBill;
    private Bolean task1;
    private Bolean task2;

    private final List<Bill> billPay = new ArrayList<Bill>();
    private final List<Bill> billRec = new ArrayList<Bill>();

    private Lock lol = new ReentrantLock();
    private final Condition c1 = lol.newCondition();
    private final Condition c2 = lol.newCondition();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task1 = new Bolean();
        task2 = new Bolean();
        String userBackendID = SaveSharedPreference.getUserName(getApplicationContext());
        List<User> users = User.find(User.class, "backend_id = ?", userBackendID);
        user = users.get(0);

        task1.lock();
        task2.lock();
        //lol.lock();
        /*try{
            mLoadPayBill = new LoadUserPayBillTask();
            mLoadRecBill = new LoadUserRecBillTask();
            mLoadPayBill.execute();
            mLoadRecBill.execute();
            while(!task1.check())
                c1.await();
            while(!task2.check())
                c2.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }*/
        //finally {
        //lol.unlock();
        //}
        mLoadPayBill = new LoadUserPayBillTask();
        mLoadRecBill = new LoadUserRecBillTask();
        mLoadPayBill.execute();
        mLoadRecBill.execute();
        //while(!(task2.check() && task1.check())) {};
        //while(!(mLoadRecBill.canContinue.check() && mLoadPayBill.canContinue.check())){};

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //List<User> users2 = User.find(User.class, "backend_id = ?", userBackendID);
        //user = users.get(0);
        setContentView(R.layout.activity_view_bills);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_view_bills)
                .setSwipeBackView(R.layout.swipeback_default);
        iOwe = (Button) findViewById(R.id.i_owe);
        oweMe = (Button) findViewById(R.id.owe_me);
        //final List<Bill> billPay = MainActivity.user.getBillPay();
        Log.d(null, "get bill pay : " + billPay.size());
        //final List<Bill> billRec = MainActivity.user.getBillRec();
        Log.d(null, "get bill rec : " + billRec.size());
        billPayCtn = new String[billPay.size()];
//        billPayCtn = new String[billPay.size()];
        billRecCtn = new String[billRec.size()];

        for (int i = 0; i < billPay.size(); i++) {
            billPayCtn[i] = billPay.get(i).toString(user.backendId);
        }
        for (int i = 0; i < billRec.size(); i++) {
            billRecCtn[i] = billRec.get(i).toString(user.backendId);
        }
        arrayAdapterBillPay = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(billPayCtn)));
        arrayAdapterBillRec = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(billRecCtn)));


        billView = (ListView) findViewById(R.id.bill_list_view);

        iOwe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payClicked = true;
                iOwe.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                oweMe.setBackgroundResource(android.R.drawable.btn_default);
                billView.setAdapter(arrayAdapterBillPay);
                SwipeDismissListViewTouchListener touchListener =
                        new SwipeDismissListViewTouchListener(
                                billView,
                                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                    @Override
                                    public boolean canDismiss(int position) {
                                        return true;
                                    }

                                    @Override
                                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                        for (int position : reverseSortedPositions) {
                                            arrayAdapterBillPay.remove(arrayAdapterBillPay.getItem(position));
                                            user.moneyPay -= billPay.get(position).amount;
                                            mRemoveBillTask = new removeBillTask(
                                                    Integer.toString(billPay.get(position).backendId),
                                                    Integer.toString(user.backendId), user.authToken,
                                                    Integer.toString(billPay.get(position).event_id));
                                            mRemoveBillTask.execute();


                                        }
                                        arrayAdapterBillPay.notifyDataSetChanged();
                                    }
                                });
                billView.setOnTouchListener(touchListener);
                // Setting this scroll listener is required to ensure that during ListView scrolling,
                // we don't look for swipes.
                billView.setOnScrollListener(touchListener.makeScrollListener());
            }
        });

        oweMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payClicked = false;
                oweMe.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                iOwe.setBackgroundResource(android.R.drawable.btn_default);
                billView.setAdapter(arrayAdapterBillRec);
                SwipeDismissListViewTouchListener touchListener =
                        new SwipeDismissListViewTouchListener(
                                billView,
                                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                    @Override
                                    public boolean canDismiss(int position) {
                                        return true;
                                    }

                                    @Override
                                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                        for (int position : reverseSortedPositions) {
                                            arrayAdapterBillRec.remove(arrayAdapterBillRec.getItem(position));
                                            user.moneyRec -= billRec.get(position).amount;
                                            mRemoveBillTask = new removeBillTask(
                                                    Integer.toString(billRec.get(position).backendId),
                                                    Integer.toString(user.backendId), user.authToken,
                                                    Integer.toString(billRec.get(position).event_id));
                                            mRemoveBillTask.execute();

                                        }
                                        arrayAdapterBillRec.notifyDataSetChanged();
                                    }
                                });
                billView.setOnTouchListener(touchListener);
                // Setting this scroll listener is required to ensure that during ListView scrolling,
                // we don't look for swipes.
                billView.setOnScrollListener(touchListener.makeScrollListener());

            }
        });
        iOwe.performClick();
        billView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewBills.this, EditBill.class);
                Bundle bundle = new Bundle();
                Bill clkedBill;
                List<BillEvent> billEvents = BillEvent.find(BillEvent.class, "backend_id = ?",
                        Integer.toString(billPay.get(position).event_id));
                billEvent = billEvents.get(0);
                if (payClicked)
                    clkedBill = billPay.get(position);
                else
                    clkedBill = billRec.get(position);

                bundle.putString("name", billEvent.name);
                bundle.putDouble("amount", clkedBill.amount);
                bundle.putString("notes", billEvent.note);
                bundle.putInt("backendId", clkedBill.backendId);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_bills, menu);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        TextView actionbarTitle = (TextView) actionbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText("View Bills");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(actionbar, params);
        return true;
    }

    public class removeBillTask extends AsyncTask<Void, Void, Boolean> {
        private String billId;
        private String userId;
        private String password;
        private String eventId;
        private Bolean f;
        private Bolean canContinue;

        removeBillTask(String b, String u, String p, String e) {
            billId = b;
            userId = u;
            password = p;
            eventId = e;
            f = new Bolean();
            canContinue = new Bolean();
            canContinue.lock();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final String TAG = "REMOVE_BILL_TASK";

            Backend.destroyBill(billId, userId, password, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(Object result) {
                    final String message = (String) result;
                    Log.d(TAG, message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });

                    f.release();
                    canContinue.release();
                }

                @Override
                public void onRequestFailed(final String message) {
                    Log.d(TAG, message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    f.lock();
                    canContinue.release();
                }
            });
            while (canContinue.check()) {
            }
            ;
            return f.check();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                List<BillEvent> events = BillEvent.find(BillEvent.class, "backend_id = ?", eventId);

                List<Bill> bills = Bill.find(Bill.class, "backend_id = ?", billId);
                //events.get(0).totalAmount -= bills.get(0).amount;

                for (Bill i : bills)
                    i.delete();

                finish();

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Already deleted.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public class LoadUserPayBillTask extends AsyncTask<Void, Void, Boolean> {

        private final Bolean canContinue;
        private final Bolean flag;

        LoadUserPayBillTask() {
            Log.d(null, "created a new billTask");
            canContinue = new Bolean();
            flag = new Bolean();
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            //while(!isCancelled()){
            final String TAG = "LOGIN_ACTIVITY_LB";
            Log.d(TAG, "billTask in");
            Backend.loadUserPayBill(user, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(Object result) {
                    flag.release();
                    final List<Bill> resultBills = (List<Bill>) result;
                    final List<Bill> allBills = Bill.find(Bill.class,
                            StringUtil.toSQLName("creditor_id") + " = ?",
                            Integer.toString(user.backendId));
                    Log.d(TAG, "BillList get success. User: " + user.toString());
                    for (Bill i : allBills)
                        i.delete();
                    for (Bill i : resultBills) {
                        //if (user.getAllBillId().contains(i.backendId)) {
                        Bill newBill = new Bill();
                        newBill.copy(i);
                        newBill.save();
                        billPay.add(newBill);
                        Log.d(TAG, "new bill saved: " + newBill.toString());
                        //}
                    }

                    task1.release();
                    //c1.signal();
                    //canContinue.release();
                    //Log.d(TAG, "move on to main");
                }

                @Override
                public void onRequestFailed(final String message) {
                    flag.lock();
                    Log.d(TAG, "Received error from Backend: " + message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    task1.release();
                    //canContinue.release();
                    //c1.signal();
                }
            });

            //while(canContinue.check()){Log.d(null, "looping");};
            return true;
        }

        /*@Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.d(null, "post1 success");
                c1.signal();
                //task1.release();
                finish();
            } else {
                Log.d(null, "post1 failure");
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }*/
    }

    public class LoadUserRecBillTask extends AsyncTask<Void, Void, Boolean> {

        private final Bolean canContinue;
        private final Bolean flag;

        LoadUserRecBillTask() {
            Log.d(null, "created a new billRecTask");
            canContinue = new Bolean();
            flag = new Bolean();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //while(!isCancelled()){
            final String TAG = "LOGIN_ACTIVITY_LB";
            Log.d(TAG, "billRecTask in");
            Backend.loadUserRecBill(user, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(Object result) {
                    //Log.d(null, "5");
                    flag.release();
                    final List<Bill> resultBills = (List<Bill>) result;
                    final List<Bill> allBills = Bill.find(Bill.class,
                            StringUtil.toSQLName("creditor_id") + " = ?",
                            Integer.toString(user.backendId));
                    Log.d(TAG, "*****" + allBills.toString());
                    //final List<Integer> allBillsId = new ArrayList<Integer>();
                    for (Bill i : allBills) {
                        //allBillsId.add(i.backendId);
                        i.delete();
                    }
                    Log.d(TAG, "BillList get success. User: " + user.toString());
                    for (Bill i : resultBills) {
                        Bill newBill = new Bill();
                        newBill.copy(i);
                        newBill.save();
                        billRec.add(newBill);
                        Log.d(TAG, "new bill saved: " + newBill.toString());
                    }
                    task2.release();
                    Log.d(TAG, "LB_@@@@@@");
                    //c2.signal();
                    //canContinue.release();
                    //Log.d(TAG, "move on to main");
                }

                @Override
                public void onRequestFailed(final String message) {
                    flag.lock();
                    Log.d(TAG, "Received error from Backend: " + message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    task2.release();
                    //canContinue.release();
                    //c2.signal();
                }

            });
            //while(canContinue.check()){Log.d(null, "looping");};

            return true;
        }

        /*@Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.d(null, "post2 success");
                c2.signal();
                finish();
            } else {
                Log.d(null, "post2 failure");
            }
        }*/
    }

//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        switch (id) {
//            case R.id.edit_bill:
//                Intent intent = new Intent(ViewBills.this, EditBill.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("billName", mEmailView.getText().toString());
//                bundle.putString("password", mPasswordView.getText().toString());
//                intent.putExtras(bundle);
//                startActivity(intent);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}
