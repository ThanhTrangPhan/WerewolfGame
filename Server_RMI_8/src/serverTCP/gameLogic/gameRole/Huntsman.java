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
public class Huntsman implements role {
    private static final String NAME = "Huntsman";
    private int chosenPlayer;
    private int huntsman;

    public Huntsman() {
    }
    
    
    
    public static String getNAME() {
        return NAME;
    }
    public void choosePlayer(int playerId){
        chosenPlayer = playerId;
    }
    
    
}
