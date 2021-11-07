/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import static Controller.DAO.conn;
import Model.Player;
import Model.PlayerStat;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
            while (rs.next()) {
                res.setTotalGameMatch(rs.getInt("totalMatch"));
                int winMatch = rs.getInt("totalWin");
                if(res.getTotalGameMatch() ==0 ){
                    res.setWinrate(0.0);
                } else res.setWinrate((winMatch / res.getTotalGameMatch()) * 100);
            }
            ps = conn.prepareStatement(sql2);
            ps.setInt(1, playerid);
            rs = ps.executeQuery();
            while (rs.next()) {
                res.setMostUseRole(rs.getString("r"));
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }
    
    public LinkedHashMap<Player,PlayerStat>  getScoreboard() {
        LinkedHashMap<Player,PlayerStat> res = new LinkedHashMap<>();

        String sql1 = "SELECT tblPlayer.*, (SELECT COUNT(*) FROM tblRecord "
                + "WHERE tblPlayer.id=tblRecord.playerID) AS totalMatch,"
                + "(SELECT COUNT(*) FROM tblRecord WHERE tblPlayer.id=tblRecord.playerID"
                + " AND tblRecord.status LIKE 'win') AS totalWin, "
                + "(SELECT totalWIn)/(SELECT totalMatch)*100 as winRate "
                + "FROM tblPlayer ORDER BY winRate DESC, totalMatch DESC; ";

        try {
            PreparedStatement ps = conn.prepareStatement(sql1); 
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Player p = new Player();
                p.setId(rs.getInt("tblPlayer.id"));
                p.setName(rs.getString("tblPlayer.name"));
                p.setPhone(rs.getString("tblPlayer.phone"));
                p.setStatus(rs.getString("tblPlayer.status"));
                PlayerStat pst = new PlayerStat();
                pst.setTotalGameMatch(rs.getInt("totalMatch"));
                pst.setWinrate(rs.getDouble("winRate"));
                res.put(p, pst);
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }
}
