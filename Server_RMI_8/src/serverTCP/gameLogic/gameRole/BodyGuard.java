/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package role;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demo
 */
public class BodyGuard implements role{
    public static final String NAME = "The BodyGuard";
    private int prePlayerProtected;
    public static String getNAME() {
        return NAME;
    }
    // to set the previous player that bodyguard protected, 
    public int getPrePlayerProtected() {
        return prePlayerProtected;
    }

    public void setPrePlayerProtected(int prePlayerProtected) {
        this.prePlayerProtected = prePlayerProtected;
    }
    
    
    
}
