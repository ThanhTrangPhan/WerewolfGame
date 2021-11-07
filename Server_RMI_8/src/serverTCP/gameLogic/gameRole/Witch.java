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
public class Witch implements role{
    private int  saveId;
    private int poisonId;
    private int healId; 
    public static final String NAME = "The Witch";

    public static String getNAME() {
        return NAME;
    }

    public int getSaveId() {
        return saveId;
    }

    public void setSaveId(int saveId) {
        this.saveId = saveId;
    }

    public int getPoisonId() {
        return poisonId;
    }

    public void setPoisonId(int poisonId) {
        this.poisonId = poisonId;
    }
}
