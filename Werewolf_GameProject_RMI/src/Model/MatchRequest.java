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
public class MatchRequest implements  Serializable{

    private Player sender;
    private Player receiver;
    private String roomID;
    private int status;

    public MatchRequest() {
    }

    public MatchRequest(Player sender, Player receiver, String roomID) {
        this.sender = sender;
        this.receiver = receiver;
        this.roomID = roomID;
        status = -1; 
    }

    public Player getSender() {
        return sender;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public void setReceiver(Player receiver) {
        this.receiver = receiver;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
}
