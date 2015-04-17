package com.example.hjcyz1991.project407.Model;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Date;
import com.orm.SugarRecord;

/**
 * Created by Winston on 4/12/15.
 */
public class User extends SugarRecord<User>{
    public int backendId;

    public Date created_at;

    public String name;
    public String email;

    public String authToken;
    public String authTokenConfirm;
    public Date tokenExpiration;

    public HashSet<User> friends;

    public HashSet<Bill> billPay;
    public HashSet<Bill> billRec;
    public HashSet<Bill> billSettled;

    public HashSet<BillEvent> eventPay;
    public HashSet<BillEvent> eventRec;

    public double moneyPay;
    public double moneyRec;

    File avatar;

    public User(){
        name = "";
        email = "";
        created_at = new Date();
        tokenExpiration = new Date();

        friends = new HashSet<User>();
        billPay = new HashSet<Bill>();
        billRec = new HashSet<Bill>();
        billSettled = new HashSet<Bill>();

        eventPay = new HashSet<BillEvent>();
        eventRec = new HashSet<BillEvent>();

        moneyPay = 0.0;
        moneyRec = 0.0;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t name: " + name);
        sb.append("\n\t email: "+ email);
        sb.append("\n\t id: " + backendId);
        sb.append("\n\t created at: " + created_at);
        //sb.append("\n\t authToken: " + authToken);
        //sb.append("\n\t tokenExpiration: " + tokenExpiration.toString());

        return sb.toString();
    }

}
