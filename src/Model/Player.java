/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author demo
 */
public class Player implements  Serializable{
    private int id;
    private String name;
    private String password;
    private String phone;
    private String status;
    private List<Player> friend;
    
    public Player() {
    }

    public Player(int id, String name, String password, String phone, String status, List<Player> friend) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.status = status;
        this.friend = friend;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Player> getFriend() {
        return friend;
    }

    public void setFriend(List<Player> friend) {
        this.friend = friend;
    }

    
    
}
