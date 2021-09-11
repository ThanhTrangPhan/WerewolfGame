/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Group;
import Model.GroupMember;
import Model.Player;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import Model.Record;

/**
 *
 * @author Admin
 */
public class RecordDAO extends DAO {

    public ArrayList<Record> viewRecordBasedOnPlayer(int playerID) {
        ArrayList<Record> result = new ArrayList<Record>();
        String sql = "SELECT * FROM tblrecord WHERE tblrecord.tblplayerid=?; ";
        result = fetchRecord(playerID, sql);
        return result;
    }

    // search the record based on the GameMatch ID
    public ArrayList<Record> viewRecordBasedOnMatch(int matchID) {
        ArrayList<Record> result = new ArrayList<Record>();
        String sql = "SELECT tblRecord.* FROM tblRecord,tblGameMatch WHERE tblRecord.gameMatchID=? "
                + "AND tblRecord.gameMatchID = tblGameMatch.id";
        fetchRecord(matchID, sql);
        return result;
    }

    public ArrayList<Record> viewRecordBasedOnGroup(int groupID) {
        ArrayList<Record> result = new ArrayList<Record>();
        String sql = "SELECT tblRecord.* "
                + "FROM ((tblGroupMember  INNER JOIN tblRecord ON  tblGroupMember.GroupID = ? "
                + "AND tblGroupMember.id = tblRecord.groupMemberID)"
                + "INNER JOIN tblPlayer on tblRecord.playerID=tblPlayer.id)"
                + "ORDER BY tblRecord.id ASC; ";
        result = fetchRecord(groupID, sql);
        return result;
        
    }
    
    private ArrayList<Record> fetchRecord(int keyID, String sql){
        ArrayList<Record> result = new ArrayList<Record>();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, keyID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Record r = new Record();
                r.setId(rs.getInt("id"));
                r.setRole(rs.getString("role"));
                r.setStatus(rs.getString("status"));
                r.setMember(new GroupMember());
                r.getMember().setId(rs.getInt("groupMemberID"));
                r.setPlayer(new Player());
                r.getPlayer().setId(rs.getInt("playerID"));

                result.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
