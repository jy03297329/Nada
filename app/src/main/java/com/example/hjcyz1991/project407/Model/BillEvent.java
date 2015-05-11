package com.example.hjcyz1991.project407.Model;

import com.orm.SugarRecord;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Winston on 4/15/15.
 */
public class BillEvent extends SugarRecord<BillEvent> {

    public int backendId;
    public int creditorId;
    public String name;
    public String note;
    public double total;
    public String created_at;

    //public HashSet<Bill> bills; //debtor id, amount

    public double totalAmount;

    public BillEvent(){
        backendId = 0;
        total = 0.0;
        creditorId = 0;
        name = new String();
        note = new String();
        created_at = new String();
    }

    public void copy(BillEvent i){
        backendId = i.backendId;
        creditorId = i.creditorId;
        name = new String(i.name);
        note = new String(i.note);
        created_at = new String(i.created_at);
    }

    public List<Bill> getBills(){
        return Bill.find(Bill.class, "event_id = ?", Integer.toString(this.backendId));
    }

}
