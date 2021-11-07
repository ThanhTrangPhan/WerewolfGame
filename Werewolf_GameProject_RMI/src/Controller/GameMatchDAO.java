/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import static Controller.DAO.conn;
import Model.GameMatch;
import Model.Record;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;


/**
 *
 * @author demo
 */
public class GameMatchDAO extends DAO {

    public GameMatchDAO() {
        super();
    }

    //update the record in 
    public boolean updateGameMatch(GameMatch match) {
        String sqlUpdateMatch = "INSERT INTO tblGameMatch(tblGameMatch.description,"
                + "tblGameMatch.type,tblGameMatch.timeStarted,tblGameMatch.timeEnded, tblGameMatch.winnerSide)"
                + "VALUES (?,?,?,?,? )";
        String sqlUpdateRecord ="INSERT INTO tblRecord(tblRecord.role,tblRecord.status,"
                + "zoomzoomzozotblRecord.playerID,tblRecord.groupMemberID,tblRecord.gameMatchID)"
                + "VALUES (?,?,?,?,? )";
        boolean res = true;
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(sqlUpdateMatch, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, match.getDescription());
            ps.setString(2, match.getType());
            ps.setString(3, match.getTimeStarted());
            ps.setString(4, String.valueOf(java.time.LocalDateTime.now()));
            ps.setString(5, match.getWinnerSide());

            ps.executeUpdate();
            ResultSet match_id = ps.getGeneratedKeys();
            if (match_id.next()) {
                match.setId(match_id.getInt(1));

                //Them vao bang Record 
                for (Record detail : match.getRecord()) {
                    ps = conn.prepareStatement(sqlUpdateRecord,Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, detail.getRole());
                    ps.setString(2, detail.getStatus());
                    ps.setInt(3, detail.getPlayer().getId());
                    if("Group Match".equals(match.getType())){
                        ps.setInt(4, detail.getMember().getId());
                    } else{
                        ps.setNull(4,java.sql.Types.INTEGER);
                    }                   
                    ps.setInt(5, match.getId());
                    ps.executeUpdate();
                    ResultSet r = ps.getGeneratedKeys();
                    if(r.next()){
                        detail.setId(r.getInt(1));
                    }
                    
                }

            }
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // setting the game
//    public boolean setting(GameMatch match) {
//        String sql = "{call setting(?,?,?,?)}";
//        try {
//            CallableStatement ps = (CallableStatement) conn.prepareCall(sql);
//            ps.setInt(1, match.getMaxPlayer());
//            ps.setString(2, match.getDescription());
//            ps.setString(3, match.getType());
//            ps.setInt(4, match.getId());
//
//            ps.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    // Search the match based on the name 
    public ArrayList<GameMatch> searchMatch(String key) {
        ArrayList<GameMatch> res = new ArrayList<>();
        String sql = "SELECT * FROM tblGameMatch WHERE tblGameMatch.type LIKE ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                GameMatch match = new GameMatch();
                match.setId(rs.getInt("id"));
                match.setDescription(rs.getString("description"));
                match.setType(rs.getString("type"));
                match.setTimeStarted(rs.getString("timeStarted"));
                match.setTimeEnded(rs.getString("timeEnded"));
                match.setWinnerSide("winnerSide");
                res.add(match);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
