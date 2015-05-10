package com.example.hjcyz1991.project407;

/**
 * Created by Winston on 5/9/15.
 */
public class Bolean {
    private boolean flag;
    Bolean(){
        flag = false;
    }
    public boolean check(){
        return flag;
    }
    public void lock(){
        flag = false;
    }
    public void release(){
        flag = true;
    }
}
