/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;

/**
 *
 * @author demo
 */
public class ObjectWrapper implements Serializable {

    public static final int CREATE_GROUP = 1;
    public static final int REPLY_CREATE_GROUP = 2;
    public static final int SEARCH_GROUP = 3;
    public static final int REPLY_SEARCH_GROUP = 4;
    public static final int CANCLE_MEMBER = 5;
    public static final int REPLY_CANCLE_MEMBER = 6;
    public static final int SEARCH_MEMBER = 7;
    public static final int REPLY_SEARCH_MEMBER = 8;
    public static final int ADD_MEMBER = 9;
    public static final int REPLY_ADD_MEMBER = 10;
    public static final int SERVER_INFORM_CLIENT_NUMBER = 11;
    public static final int SEARCH_GROUP_JOINED = 12;
    public static final int REPLY_GROUP_JOINED = 13;
    public static final int FRIEND_LIST = 14;
    public static final int REPLY_FRIEND_LIST = 15;
    public static final int PLAYER_ONLINE = 16;
    public static final int SEARCH_PLAYER_ID = 17;
    public static final int REPLY_ONLINE = 18;
    public static final int LOGIN = 19;
    public static final int REPLY_LOGIN = 20;
    public static final int REGISTER = 21;
    public static final int REPLY_REGISTER = 22;
    public static final int STATISTIC = 23;
    public static final int REPLY_STATISTIC = 24;
    public static final int GLOBAL_SCOREBOARD = 25;
    public static final int INVITE_MATCH = 27;
    public static final int REPLY_INVITE_MATCH = 28;
    public static final int LOAD_FRIEND_REQUEST = 29;
    public static final int REPLY_LOAD_FRIEND_REQUEST = 30;
    public static final int ACCEPT_FRIEND_REQUEST = 31;
    public static final int REJECT_FRIEND_REQUEST = 32;
    public static final int REPLY_ANS_FRIEND_REQUEST = 33;
    public static final int SEND_FRIEND_REQUEST = 34;
    public static final int REPLY_SEND_FRIEND_REQUEST = 35;

    private int performative;
    private Object data;

    public ObjectWrapper() {
        super();
    }

    public ObjectWrapper(int performative, Object data) {
        super();
        this.performative = performative;
        this.data = data;
    }

    public int getPerformative() {
        return performative;
    }

    public void setPerformative(int performative) {
        this.performative = performative;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
