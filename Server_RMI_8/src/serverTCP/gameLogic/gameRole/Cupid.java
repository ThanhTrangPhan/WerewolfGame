/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverTCP.gameLogic.gameRole;

import java.util.ArrayList;

/**
 *
 * @author demo
 */
public class Cupid implements role {
    public static final String NAME = "The Cupid";
    private ArrayList<Integer> coupleId ;
    private int playerId;
    public static String getNAME() {
        return NAME;
    }

    public Cupid(int playerId) {
        this.playerId = playerId;
    }

    public ArrayList<Integer> getCoupleId() {
        return coupleId;
    }

    public void setCoupleId(ArrayList<Integer> coupleId) {
        this.coupleId = coupleId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    
    
}
