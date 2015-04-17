package com.example.hjcyz1991.project407.Model;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by Winston on 4/15/15.
 */
public class Bill extends SugarRecord<Bill> {
    public int backendId;

    public Date created_at;

    public User debtor;
    public User creditor;

    public BillEvent event;

    public double moneyAmt;

    public Bill(){
        created_at = new Date();
        debtor = new User();
        creditor = new User();
        event = new BillEvent();
        moneyAmt = 0.0;
    }
}


