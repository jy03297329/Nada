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
    //public int userId;
    public String password;

    public int creditorId;

    public String name;
    public String note;
    public int total;
    public Date created_at;

    //public HashSet<Bill> bills; //debtor id, amount

    public double totalAmount;

    public BillEvent(){
        backendId = 0;
        total = 0;
        created_at = new Date();
    }

    public List<Bill> getBills(){
        return Bill.find(Bill.class, "event_id = ?", Integer.toString(this.backendId));
    }

}
