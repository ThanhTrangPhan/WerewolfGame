/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverTCP.gameLogic.gameRole;

/**
 *
 * @author demo
 */
public class Werewolf implements role{
    private static final String NAME = "Werewolf";
    private int playerId;
    private int chosenPlayerID; 

    public Werewolf() {
    }

    public Werewolf(int playerId) {
        this.playerId = playerId;
    }

    public int getChosenPlayerID() {
        return chosenPlayerID;
    }

    public void setChosenPlayerID(int chosenPlayerID) {
        this.chosenPlayerID = chosenPlayerID;
    }

    
    public static String getNAME() {
        return NAME;
    }
    
    public boolean isVote(){
        return true;
    }
    
    
}
