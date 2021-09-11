/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import Model.Player;

/**
 *
 * @author Admin
 */
public class PlayerDAO extends DAO {

    public ArrayList<Player> searchPlayers(String key) {
        ArrayList<Player> result = new ArrayList<>();
        String sql = "SELECT * FROM tblPlayer WHERE tblPlayer.name LIKE ? ";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Player p = new Player();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setPassword(rs.getString("password"));
                p.setPhone(rs.getString("phone"));
                p.setStatus(rs.getString("status"));
                result.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Player> searchPlayers(int key) {
        ArrayList<Player> result = new ArrayList<>();
        String sql = "SELECT * FROM tblPlayer WHERE tblPlayer.id=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, key);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Player p = new Player();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setPassword(rs.getString("password"));
                p.setPhone(rs.getString("phone"));
                p.setStatus(rs.getString("status"));
                result.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public boolean editInfo(Player player) {
        String sql = "UPDATE tblPlayer SET name=?, password?, phone=?, status=? WHERE id=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, player.getName());
            ps.setString(2, player.getPassword());
            ps.setString(3, player.getPhone());
            ps.setString(4, player.getStatus());
            ps.setInt(7, player.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean register(Player player) {
        String sql = "INSERT INTO tblplayer(name, password,phone,status) VALUES(?,?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, player.getName());
            ps.setString(2, player.getPassword());
            ps.setString(3, player.getPhone());
            ps.setString(4, player.getStatus());
            ps.executeUpdate();
            //get id of the new inserted client
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                player.setId(generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addFriend(Player friend, Player friend2) {
        String sql = "INSERT INTO tblfriendlist(player1,player2) VALUES(?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            // make sure dont have duplicate data
            // like (1,2) and (2,1)
            ps.setInt(1, Integer.min(friend.getId(), friend2.getId()));
            ps.setInt(2, Integer.max(friend.getId(), friend2.getId()));

            ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean cancelFriend(Player friend, Player friend2) {
        String sql = "DELETE FROM tblfriendlist WHERE player1=? AND player2=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, Integer.min(friend.getId(), friend2.getId()));
            ps.setInt(2, Integer.max(friend.getId(), friend2.getId()));

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean checkLogin(String name, String pass) {
        String sql = "SELECT * FROM tblplayer WHERE name = ? AND password = ? ";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, pass);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
