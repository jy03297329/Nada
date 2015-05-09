package com.example.hjcyz1991.project407.Model;

import com.orm.SugarRecord;

/**
 * Created by Winston on 5/2/15.
 */
public class Friendship extends SugarRecord<Friendship> {
    public int backendId;
    public int userId;
    public int friendId;
    public User user;
    public User friend;

    public Friendship(){
        backendId = 0;
        userId = 0;
        friendId = 0;
        user = new User();
        friend = new User();
    }
    public Friendship(User user1, User user2, int id){
        backendId = id;
        user = new User();
        user.copy(user1);
        userId = user1.backendId;
        friend = new User();
        friend.copy(user2);
        friendId = user2.backendId;
    }
    public Friendship(User user1, User user2){
        backendId = 0;
        user = new User();
        user.copy(user1);
        userId = user1.backendId;
        friend = new User();
        friend.copy(user2);
        friendId = user2.backendId;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t user: " + userId);
        sb.append("\n\t friend: "+ friendId);
        sb.append("\n\t backendId: " + backendId);
        //sb.append("\n\t password: " + authToken);
        //sb.append("\n\t moneyPay: " + moneyPay);
        return sb.toString();
    }
}
