/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Model.GameMatch;
import Model.Group;
import Model.Player;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class Record implements Serializable {
    private static final long serialVersionUID = 20210811004L;
    private int id;
    private String role;
    private String status;
    private Player player;
    private GroupMember member;

    public Record(){
        super();
    }

    public Record(int id, String role, String status, Player player, GroupMember member) {
        this.id = id;
        this.role = role;
        this.status = status;
        this.player = player;
        this.member = member;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public GroupMember getMember() {
        return member;
    }

    public void setMember(GroupMember member) {
        this.member = member;
    }

    
}
    
