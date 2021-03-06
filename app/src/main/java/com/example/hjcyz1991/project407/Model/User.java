package com.example.hjcyz1991.project407.Model;

import android.util.Log;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Date;
import java.util.List;

import com.orm.StringUtil;
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

    public double moneyPay;
    public double moneyRec;

    //File avatar;

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

    public List<Integer> getFriendsId(){
        List<User> friends = this.getFriends();
        List<Integer> friendsId = new ArrayList<Integer>();

        for(User i : friends){
            friendsId.add(i.backendId);
            Log.d("IN USER CLASS", "friend: " + i.toString());
        }
        return friendsId;
    }

    public List<User> getFriends(){
        Log.d("GETTING FRIEND: ", "getting friends");
        return Friendship.find(User.class, "backend_id", Integer.toString(this.backendId));
        //return result;
    }

    public List<Bill> getBillPay(){
        //Log.d(null, StringUtil.toSQLName("debtor_id"));
        String query = new StringBuilder()
                .append(StringUtil.toSQLName("debtor_id"))
                .append(" = ?").toString();
        List<Bill> result = Bill.find(Bill.class, query,
                Integer.toString(this.backendId));
        for(Bill i : result){
            if(i.settled)
                result.remove(i);
        }
        return result;
    }
    public List<Bill> getBillRec(){
        String query = new StringBuilder()
                .append(StringUtil.toSQLName("creditor_id"))
                .append(" = ?").toString();
        List<Bill> result = Bill.find(Bill.class, query,
                Integer.toString(this.backendId));
        for(Bill i : result){
            if(i.settled)
                result.remove(i);
        }
        return result;
    }
    public List<Bill> getBillSettled(){
        List<Bill> result = this.getAllBill();

        for(Bill i : result){
            if(!i.settled)
                result.remove(i);
        }
        return result;
    }

    public List<Bill> getBillUnsettled(){
        List<Bill> result = this.getAllBill();

        for(Bill i : result){
            if(i.settled)
                result.remove(i);
        }
        return result;
    }

    public List<Bill> getAllBill(){
        String query1 = new StringBuilder()
                .append(StringUtil.toSQLName("debtor_id"))
                .append(" = ?").toString();
        List<Bill> result = Bill.find(Bill.class, query1,
                Integer.toString(this.backendId));
        String query2 = new StringBuilder()
                .append(StringUtil.toSQLName("creditor_id"))
                .append(" = ?").toString();
        result.addAll(Bill.find(Bill.class, query2,
                Integer.toString(this.backendId)));
        return result;
    }

    public List<Integer> getAllBillId(){
        List<Bill> result = this.getAllBill();
        List<Integer> resultId = new ArrayList<Integer>();
        for(Bill i : result)
            resultId.add(i.backendId);

        return resultId;
    }

    public void copy(User user){
        backendId = user.backendId;
        name = user.name;
        email = user.email;
        authToken = user.authToken;
        authTokenConfirm = user.authTokenConfirm;
        created_at = user.created_at;
        moneyRec = user.moneyRec;
        moneyPay = user.moneyPay;
    }

    public List<User> getAll(){
        return User.listAll(User.class);
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
