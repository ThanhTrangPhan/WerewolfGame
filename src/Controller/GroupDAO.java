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
import java.util.List;

/**
 *
 * @author demo
 */
public class GroupDAO extends DAO {

    public GroupDAO() {
        super();
    }

    // create a new group
    public boolean createGroup(Group group) {
        String sqlcreate = "INSERT INTO tblGroup(tblGroup.name,tblGroup.founderName,tblGroup.timeStarted)"
                + " VALUES (?, ?,?)";
        String sqlAddMember = "INSERT INTO tblGroupMember(tblGroupMember.timeJoined,tblGroupMember.GroupID,tblGroupMember.playerID) "
                + "VALUES (?, ?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlcreate, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, group.getName());
            ps.setString(2, group.getFounderName());
            ps.setString(3, group.getTimeStarted());

            ps.executeUpdate();
            ResultSet groupID = ps.getGeneratedKeys();
            if (groupID.next()) {
                group.setId(groupID.getInt(1));
                ps = conn.prepareStatement(sqlAddMember, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, String.valueOf(java.time.LocalDateTime.now()));
                ps.setInt(2, group.getId());
                ps.setInt(3, group.getMember().get(0).getPlayer().getId());
                ps.executeUpdate();

                // id of new member
                ResultSet member_id = ps.getGeneratedKeys();
                if (member_id.next()) {
                    group.getMember().get(0).setId(member_id.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    
    public ArrayList<Group> searchAllGroup() {
        ArrayList<Group> res = new ArrayList<>();
        String sql = "SELECT * FROM tblGroup ";
        String sql2 = "SELECT tblGroupMember.*,tblPlayer.* FROM tblPlayer,tblGroupMember,tblGroup WHERE tblGroup.id = ? "
                + "AND tblGroup.id=tblGroupMember.GroupID AND tblGroupMember.playerID=tblPlayer.id";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Group group = new Group();
                group.setId(rs.getInt("id"));
                group.setName(rs.getString("name"));
                group.setFounderName(rs.getString("founderName"));
                group.setTimeStarted(rs.getString("timeStarted"));
                res.add(group);
            }
            for (Group g : res) {
                ps = conn.prepareStatement(sql2);
                ps.setInt(1, g.getId());
                rs = ps.executeQuery();
                List<GroupMember> lg = new ArrayList<>();
                while (rs.next()) {
                    GroupMember m = new GroupMember();
                    m.setId(rs.getInt("id"));
                    m.setTimeJoined(rs.getString("timeJoined"));
                    
                    Player p = new Player();
                    p.setId(rs.getInt("tblPlayer.id"));
                    p.setName(rs.getString("tblPlayer.name"));
                    m.setPlayer(p);
                    lg.add( m);
                }
                g.setMember(lg);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    //search a group 
    public ArrayList<Group> searchGroup(String key) {
        ArrayList<Group> res = new ArrayList<>();
        String sql = "SELECT * FROM tblGroup WHERE tblGroup.name LIKE ?";
        String sql2 = "SELECT tblGroupMember.*,tblPlayer.* FROM tblPlayer,tblGroupMember,tblGroup WHERE tblGroup.id = ? "
                + "AND tblGroup.id=tblGroupMember.GroupID AND tblGroupMember.playerID=tblPlayer.id";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Group group = new Group();
                group.setId(rs.getInt("id"));
                group.setName(rs.getString("name"));
                group.setFounderName(rs.getString("founderName"));
                group.setTimeStarted(rs.getString("timeStarted"));
                res.add(group);
            }
            for (Group g : res) {
                ps = conn.prepareStatement(sql2);
                ps.setInt(1, g.getId());
                rs = ps.executeQuery();
                List<GroupMember> lg = new ArrayList<>();
                while (rs.next()) {
                    GroupMember m = new GroupMember();
                    m.setId(rs.getInt("id"));
                    m.setTimeJoined(rs.getString("timeJoined"));
                    
                    Player p = new Player();
                    p.setId(rs.getInt("tblPlayer.id"));
                    p.setName(rs.getString("tblPlayer.name"));
                    m.setPlayer(p);
                    lg.add( m);
                }
                g.setMember(lg);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    public ArrayList<Group> searchGroupJoined(int id) {
        ArrayList<Group> res = new ArrayList<>();
        String sql = "SELECT * FROM tblGroup,tblGroupMember WHERE tblGroupMember.playerID = ? AND tblGroupMember.GroupID = tblGroup.id";
        String sql2 = "SELECT tblGroupMember.*,tblPlayer.* FROM tblPlayer,tblGroupMember,tblGroup WHERE tblGroup.id = ? "
                + "AND tblGroup.id=tblGroupMember.GroupID AND tblGroupMember.playerID=tblPlayer.id";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Group group = new Group();
                group.setId(rs.getInt("tblGroup.id"));
                group.setName(rs.getString("tblGroup.name"));
                group.setFounderName(rs.getString("tblGroup.founderName"));
                group.setTimeStarted(rs.getString("tblGroup.timeStarted"));
                res.add(group);
            }
            for (Group g : res) {
                ps = conn.prepareStatement(sql2);
                ps.setInt(1, g.getId());
                rs = ps.executeQuery();
                List<GroupMember> lg = new ArrayList<>();
                while (rs.next()) {
                    GroupMember m = new GroupMember();
                    m.setId(rs.getInt("tblGroupMember.id"));
                    m.setTimeJoined(rs.getString("tblGroupMember.timeJoined"));
                    
                    Player p = new Player();
                    p.setId(rs.getInt("tblPlayer.id"));
                    p.setName(rs.getString("tblPlayer.name"));
                    m.setPlayer(p);
                    lg.add( m);
                }
                g.setMember(lg);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    } 
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
    
    public boolean addMember(GroupMember member,int g) {
        String sql = "INSERT INTO tblGroupMember(tblGroupMember.timeJoined,tblGroupMember.GroupID,tblGroupMember.playerID) "
                + "VALUES (?, ?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, String.valueOf(java.time.LocalDateTime.now()));
            ps.setInt(2, g);
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

    //search all member in a group
    public ArrayList<GroupMember> searchMember(String key,int groupID) {
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
