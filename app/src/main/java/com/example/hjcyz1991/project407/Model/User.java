package com.example.hjcyz1991.project407.Model;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Date;
import java.util.List;

import com.orm.SugarRecord;

/**
 * Created by Winston on 4/12/15.
 */
public class User extends SugarRecord<User>{
    public int backendId;

    //public Date created_at;

    public String created_at;
    public String name;
    public String email;

    public String authToken;
    public String authTokenConfirm;
    public Date tokenExpiration;
    //public HashSet<BillEvent> eventPay;
    //public HashSet<BillEvent> eventRec;

    public double moneyPay;
    public double moneyRec;

    File avatar;

    public User(){
        backendId = 0;

        name = "";
        email = "";
        //created_at = new Date();
        created_at = "";
        tokenExpiration = new Date();
        //eventPay = new HashSet<BillEvent>();
        //eventRec = new HashSet<BillEvent>();
        moneyPay = 0.0;
        moneyRec = 0.0;
    }

    public List<User> getFriends(){

        return friendship.findWithQuery(User.class, "Select friend from friendship where user = ?",
                Long.toString(this.getId()));
        //result.addAll(friendship.find(User.class, "user2 = ?", this.getId().toString()));
        //return result;
    }

    public List<Bill> getBillPay(){
        return Bill.find(Bill.class, "debtor_id = ? and settled = ?",
                Integer.toString(this.backendId), "false");
    }
    public List<Bill> getBillRec(){
        return Bill.find(Bill.class, "creditor_id = ? and settled = ?",
                Integer.toString(this.backendId), "false");
    }
    public List<Bill> getBillSettled(){
        List<Bill> result = Bill.find(Bill.class, "debtor_id = ? and settled = ?",
                Integer.toString(this.backendId), "true");
        result.addAll(Bill.find(Bill.class, "creditor_id = ? and settled = ?",
                Integer.toString(this.backendId), "true"));
        return result;
    }

    public void copy(User user){
        backendId = user.backendId;
        name = user.name;
        email = user.email;
        authToken = user.authToken;
        authTokenConfirm = user.authTokenConfirm;
        created_at = user.created_at;
        /*if(user.friends != null)
            friends.addAll(user.friends);
        if(user.billPay != null)
            billPay.addAll(user.billPay);
        if(user.billRec != null)
            billRec.addAll(user.billRec);
        billSettled.addAll(user.billSettled);*/
        //eventPay.addAll(user.eventPay);
        //eventRec.addAll(user.eventRec);
        moneyPay = user.moneyPay;
        moneyRec = user.moneyRec;
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t name: " + name);
        sb.append("\n\t email: "+ email);
        sb.append("\n\t backendId: " + backendId);
        sb.append("\n\t password: " + authToken);
        sb.append("\n\t moneyPay: " + moneyPay);
        return sb.toString();
    }

}
