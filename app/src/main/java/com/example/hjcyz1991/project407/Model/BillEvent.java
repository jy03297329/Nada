package com.example.hjcyz1991.project407.Model;

import com.orm.SugarRecord;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by Winston on 4/15/15.
 */
public class BillEvent extends SugarRecord<BillEvent> {

    public int backendId;
    public int userId;
    public String password;

    public int creditorId;

    public String name;
    public String note;
    public Date created_at;

    public HashSet<Bill> bills; //debtor id, amount

    public double totalAmount;

    public BillEvent(){
        created_at = new Date();
    }
}
