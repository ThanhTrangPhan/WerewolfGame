/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import static Controller.DAO.conn;
import Model.GameMatch;
import Model.PlayerStat;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class PlayerStatDAO {

    // calculate a single Player Stat
    public PlayerStat getPlayerStat(int playerid) {
        PlayerStat res = new PlayerStat();
        
        String sql1 = "SELECT tblPlayer.name, (SELECT COUNT(*) FROM tblRecord WHERE tblPlayer.id = ? "
                + "AND tblPlayer.id=tblRecord.playerID) AS totalMatch,"
                + "(SELECT COUNT(*) FROM tblRecord WHERE tblPlayer.id = ? "
                + "AND tblPlayer.id=tblRecord.playerID AND tblRecord.status LIKE 'win') AS totalWin "
                + "FROM tblPlayer WHERE tblPlayer.id=?";
        
        // Find what role that player used most
        String sql2 = "SELECT tblRecord.role as r, COUNT(*) FROM tblRecord "
                + "WHERE tblRecord.playerID=? GROUP BY r  ORDER BY 2 DESC LIMIT 1 ;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql1);
            ps.setInt(1, playerid);
            ps.setInt(2, playerid);
            ps.setInt(3, playerid);
            ResultSet rs = ps.executeQuery();

            res.setTotalGameMatch(rs.getInt("totalMatch"));
            int winMatch = rs.getInt("totalWin");
            res.setWinrate((winMatch / res.getTotalGameMatch()) * 100);
            
            ps = conn.prepareStatement(sql2);
            ps.setInt(1, playerid);
            rs = ps.executeQuery();
            
            res.setMostUseRole(rs.getString("r"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }
}
