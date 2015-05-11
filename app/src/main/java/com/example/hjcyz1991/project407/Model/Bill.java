package com.example.hjcyz1991.project407.Model;

import com.orm.SugarRecord;

import java.util.Date;
import java.util.List;

/**
 * Created by Winston on 4/15/15.
 */
public class Bill extends SugarRecord<Bill> {
    public int backendId;

    public String created_at;
    public String settled_at;
    public String updated_at;
    public int debtor_id;
    public int creditor_id;
    public int event_id;
    public double amount;
    public boolean settled;

    public Bill(){
        backendId = 0;
        created_at = new String();
        settled_at = new String();
        updated_at = new String();
        debtor_id = 0;
        creditor_id = 0;
        event_id = 0;
        amount = 0.0;
        settled = false;
    }

    public void copy(Bill i){
        backendId = i.backendId;
        created_at = new String();
        created_at = i.created_at;
        debtor_id = i.debtor_id;
        creditor_id = i.creditor_id;
        event_id = i.event_id;
        amount = i.amount;
        settled = i.settled;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t ");
        sb.append("\n\t backendId: " + backendId);
        sb.append("\n\t creditor: " + creditor_id);
        sb.append("\n\t debtor: " + debtor_id);
        return sb.toString();
    }

    public String toString(int userId){
        StringBuilder sb = new StringBuilder();
        String creditor;
        String debtor;
        List<User> users1 = User.find(User.class, "backend_id = ?", Integer.toString(creditor_id));
        List<User> users2 = User.find(User.class, "backend_id = ?", Integer.toString(debtor_id));
        creditor = users1.get(0).name;
        debtor = users2.get(0).name;
        if(userId == creditor_id){
            sb.append(debtor).append(" owe you $")
                    .append(Double.toString(amount));
        }else{
            sb.append("You owe ").append(creditor).append(" $")
                    .append(Double.toString(amount));
        }
        return sb.toString();
    }
}


