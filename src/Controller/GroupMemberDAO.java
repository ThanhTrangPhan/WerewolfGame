/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import static Controller.DAO.conn;
import Model.Group;
import Model.GroupMember;
import Model.Player;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author demo
 */
public class GroupMemberDAO extends DAO {

    public GroupMemberDAO() {
        super();
    }

  
    /* cancle a membership 
    cancleMember:
    DELETE FROM tblGroupMember WHERE tblGroupMember.id = idMember;
     */
    public boolean cancleMember(GroupMember member) {
        String sql = "DELETE FROM tblGroupMember WHERE tblGroupMember.id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, member.getId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    //search all member in a group
    public ArrayList<GroupMember> searchAllMember(String key,int groupID) {
        ArrayList<GroupMember> res = new ArrayList<>();
        String sql = "SELECT tblGroupMember.*,tblPlayer.name as name,tblPlayer.id as playerid FROM tblGroupMember,tblPlayer WHERE tblPlayer.name LIKE ? "
                + "AND tblGroupMember.GroupID = ? "
                + "AND tblPlayer.id=tblGroupMember.playerID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ps.setInt(2, groupID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                GroupMember member = new GroupMember();
                member.setId(rs.getInt("tblGroupMember.id"));
                member.setTimeJoined(rs.getString("timeJoined"));
                Player p = new Player();
                p.setName(rs.getString("name"));
                p.setId(rs.getInt("playerid"));
                member.setPlayer(p);
                res.add(member);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
