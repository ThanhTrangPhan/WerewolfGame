/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverTCP.gameLogic.gameRole;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author demo
 */
public class Seer implements role{
    private Map<Integer,Boolean> verifyMap = new HashMap<>();
    private static final String NAME = "Seer";

    public Seer() {
    }

    public static String getNAME() {
        return NAME;
    }

    public Map<Integer, Boolean> getVerifyMap() {
        return verifyMap;
    }

    public void setVerifyMap(int userId,boolean isGood) {
       verifyMap.put(userId,isGood);
    }
}
