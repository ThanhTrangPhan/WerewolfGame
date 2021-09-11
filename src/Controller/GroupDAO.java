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

    

    //search a group 
    public ArrayList<Group> searchGroup(String key) {
        ArrayList<Group> res = new ArrayList<>();
        String sql = "SELECT * FROM tblGroup WHERE tblGroup.name LIKE ?";
        String sql2 = "SELECT tblGroupMember.* FROM tblGroupMember,tblGroup WHERE tblGroup.id = ? "
                + "AND tblGroup.id=tblGroupMember.GroupID";
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
                    p.setId(rs.getInt("playerID"));
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
    
    
}
