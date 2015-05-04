package com.example.hjcyz1991.project407.Model;

import com.orm.SugarRecord;

/**
 * Created by Winston on 5/2/15.
 */
public class friendship extends SugarRecord<friendship> {
    public User user;
    public User friend;
    public friendship(){
        user = null;
        friend = null;
    }
    public friendship(User user1, User user2){
        user = user1;
        friend = user2;
    }
}
