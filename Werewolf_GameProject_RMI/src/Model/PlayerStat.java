/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;

/**
 *
 * @author Admin
 */
public class PlayerStat implements  Serializable{

    //private static final long serialVersionUID = 20210811004L;
    private double winrate;
    private String mostUseRole;
    private int totalGameMatch;

    public PlayerStat() {
        super();
    }

    public double getWinrate() {
        return winrate;
    }

    public void setWinrate(double winrate) {
        this.winrate = winrate;
    }

    public String getMostUseRole() {
        return mostUseRole;
    }

    public void setMostUseRole(String mostUseRole) {
        this.mostUseRole = mostUseRole;
    }

    public int getTotalGameMatch() {
        return totalGameMatch;
    }

    public void setTotalGameMatch(int totalGameMatch) {
        this.totalGameMatch = totalGameMatch;
    }

    public PlayerStat(double winrate, String mostUseRole, int totalGameMatch) {
        this.winrate = winrate;
        this.mostUseRole = mostUseRole;
        this.totalGameMatch = totalGameMatch;
    }
}
