package com.example.hjcyz1991.project407;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.hjcyz1991.project407.Model.User;

import java.util.ArrayList;
import java.util.List;


public class AddGroups extends ActionBarActivity{

    private ListView contactsView;
    private Button group;
    private Button cancel;
    ArrayAdapter<String> contactsAdapter;
    private final int CONTACTS_SELECTED = 1;
    private int[] contactsBackendID;
    private String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_groups);
        contactsView = (ListView)findViewById(R.id.contact_listView);

        String[] items = new String[] { "Vegetables","Fruits","Flower Buds","Legumes","Bulbs","Tubers", "", "", "", "", "", "", "aaaaaaa","", "", "", "", "", "", "aaaaaaa" };


        List<User> contacts = MainActivity.user.getFriends();
        //for testing only with the temp items array
        contactsBackendID = new int[items.length];
//        contactsBackendID = new int[contacts.size()];

        names = new String[contacts.size()];

        for(int i = 0; i < contacts.size(); i++){
            contactsBackendID[i] = contacts.get(i).backendId;
            names[i] = contacts.get(i).name;
        }
        contactsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, items);

        contactsView.setAdapter(contactsAdapter);
        cancel = (Button)findViewById(R.id.button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        group = (Button) findViewById(R.id.button_group);
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = contactsView.getCheckedItemPositions();
                ArrayList<String> selectedItems = new ArrayList<String>();
                ArrayList<Integer> selectedItemsID = new ArrayList<Integer>();
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)){
                        selectedItems.add(contactsAdapter.getItem(position));
                        selectedItemsID.add(contactsBackendID[position]);
                    }
                }
                String[] outputStrArr = new String[selectedItems.size()];
                for (int i = 0; i < selectedItems.size(); i++) {
                    outputStrArr[i] = selectedItems.get(i);
                }
                Bundle bundleNames = new Bundle();
                bundleNames.putStringArray("selectedItems", outputStrArr);
                Bundle bundleID = new Bundle();
                bundleID.putIntegerArrayList("selectedItemsID", selectedItemsID);

                Intent contactsSelected = new Intent();
                contactsSelected.putExtra("contactsSelected", bundleNames);
                contactsSelected.putExtra("contactsSelectedID", bundleID);
                setResult(CONTACTS_SELECTED, contactsSelected);
                finish();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER );
        TextView actionbarTitle = (TextView)actionbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText("Add Groups");
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
