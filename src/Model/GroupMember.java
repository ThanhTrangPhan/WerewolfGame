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
public class GroupMember implements Serializable{
    private int id;
    private String timeJoined;
    private Group group;
    private Player player;

    public GroupMember() {
    }

    public GroupMember(int id, String timeJoined, Group group, Player player) {
        this.id = id;
        this.timeJoined = timeJoined;
        this.group = group;
        this.player = player;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimeJoined() {
        return timeJoined;
    }

    public void setTimeJoined(String timeJoined) {
        this.timeJoined = timeJoined;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    
}
