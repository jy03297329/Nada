package com.example.hjcyz1991.project407.Model;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by Winston on 4/15/15.
 */
public class Bill extends SugarRecord<Bill> {
    public int backendId;

    public Date created_at;
    public int debtor_id;
    //public User debtor;
    public int creditor_id;
    //public User creditor;
    public int event_id;
    public double amount;
    public boolean settled;

    public Bill(){
        backendId = 0;
        created_at = new Date();
        debtor_id = 0;
        creditor_id = 0;
        //debtor = new User();
        //creditor = new User();
        //event = new BillEvent();
        event_id = 0;
        amount = 0.0;
        settled = false;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t Bill: ");
        sb.append("\n\t backendId: " + backendId);
        sb.append("\n\t creditor: " + creditor_id);
        sb.append("\n\t debtor: " + debtor_id);
        return sb.toString();
    }
}


