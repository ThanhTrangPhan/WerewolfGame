/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package role;

/**
 *
 * @author demo
 */
public class Werewolf implements role{
    private static final String NAME = "Werewolf";
    

    public static String getNAME() {
        return NAME;
    }
    
    public boolean isVote(){
        return true;
    }
    
    
}
