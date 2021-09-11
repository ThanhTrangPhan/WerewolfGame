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

    //add a player to group 
    public boolean addMember(GroupMember member) {
        String sql = "INSERT INTO tblGroupMember(tblGroupMember.timeJoined,tblGroupMember.GroupID,tblGroupMember.playerID) "
                + "VALUES (?, ?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, String.valueOf(java.time.LocalDateTime.now()));
            ps.setInt(2, member.getGroup().getId());
            ps.setInt(3, member.getPlayer().getId());
            ps.executeUpdate();

            // id of new member
            ResultSet member_id = ps.getGeneratedKeys();
            if (member_id.next()) {
                member.setId(member_id.getInt(1));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* cancle a membership 
    cancleMember:
    DELETE FROM tblGroupMember WHERE tblGroupMember.id = idMember;
     */
    public boolean cancleMember(GroupMember member) {
        String sql = "{call cancleMember(?)}";
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

    //search a member based on name
    public ArrayList<GroupMember> searchMember(String key, Group group) {
        ArrayList<GroupMember> res = new ArrayList<>();
        String sql = "SELECT tblGroupMember.*,tblPlayer.name as name,tblPlayer.id as playerid FROM tblGroupMember,tblPlayer WHERE tblPlayer.name LIKE ? "
                + "AND tblGroupMember.GroupID = ? "
                + "AND tblPlayer.id=tblGroupMember.playerID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ps.setInt(2, group.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                GroupMember member = new GroupMember();
                member.setId(rs.getInt("tblGroupMember.id"));
                member.setTimeJoined(rs.getString("timeJoined"));
                member.setGroup(group);
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
