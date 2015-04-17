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
}
