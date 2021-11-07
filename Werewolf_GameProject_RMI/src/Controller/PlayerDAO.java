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
import java.util.HashMap;
import java.util.Map;

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

    public Player searchPlayers(int key) {
        Player p = new Player();
        String sql = "SELECT * FROM tblPlayer WHERE tblPlayer.id=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, key);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setPassword(rs.getString("password"));
                p.setPhone(rs.getString("phone"));
                p.setStatus(rs.getString("status"));
            }

            return p;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
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

    public String register(Player player) {
        String sql = "INSERT INTO tblPlayer(name, password,phone,status) VALUES(?,?,?,'FREE')";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, player.getName());
            ps.setString(2, player.getPassword());
            ps.setString(3, player.getPhone());

            ps.executeUpdate();
            //get id of the new inserted client
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                player.setId(generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
        return String.valueOf(player.getId());
    }

    public boolean addFriend(Player friend, Player friend2) {
        String sql = "INSERT INTO tblFriendList(player1,player2,sender,status,timestamp) VALUES(?,?,?,'PENDING',?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            // make sure dont have duplicate data
            // like (1,2) and (2,1)
            ps.setInt(1, Integer.min(friend.getId(), friend2.getId()));
            ps.setInt(2, Integer.max(friend.getId(), friend2.getId()));
            ps.setInt(3, friend.getId());
            ps.setString(4, String.valueOf(java.time.LocalDateTime.now()));

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateStatusFriend(Player friend, Player friend2) {
        String sql = "UPDATE tblFriendList SET status = 'AC' WHERE tblFriendList.player1 = ? AND tblFriendList.player2 = ?;";
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

    public HashMap<Player, String> showFriendRequestPending(Player player) {
        HashMap<Player, String> res = new HashMap<Player, String>();
        String sql = "SELECT * FROM tblFriendList WHERE status ='PENDING' AND (player1 = ? OR player2 = ?) AND sender != ?";
        String sql2 = "SELECT * FROM tblPlayer WHERE tblPlayer.id=? ";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, player.getId());
            ps.setInt(2, player.getId());
            ps.setInt(3, player.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Player p = new Player();
                if (player.getId() == rs.getInt("player1")) {
                    p.setId(rs.getInt("player2"));
                } else {
                    p.setId(rs.getInt("player1"));
                }
                System.out.println(p.getId());
                String time = String.valueOf(rs.getString("tblFriendList.timestamp"));
                res.put(p, time);
            }

            for (Map.Entry<Player, String> e : res.entrySet()) {
                ps = conn.prepareStatement(sql2);
                ps.setInt(1, e.getKey().getId());
                rs = ps.executeQuery();
                while (rs.next()) {
                    e.getKey().setName(rs.getString("name"));
                    e.getKey().setPhone(rs.getString("phone"));
                    e.getKey().setStatus(rs.getString("status"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean cancelFriend(Player friend, Player friend2) {
        String sql = "DELETE FROM tblFriendList WHERE player1=? AND player2=?";
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

    public ArrayList<Player> SearchFriendList(int id) {
        ArrayList<Player> friendList = new ArrayList<>();
        String sql = "SELECT * FROM tblFriendList WHERE (player1 = ? OR player2 =?) AND status ='AC'";
        String sql2 = "SELECT * FROM tblPlayer WHERE tblPlayer.id=? ";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Player p = new Player();
                if (rs.getInt("player1") == id) {
                    p.setId(rs.getInt("player2"));
                } else {
                    p.setId(rs.getInt("player1"));
                }
                friendList.add(p);
            }
            for (Player p : friendList) {
                ps = conn.prepareStatement(sql2);
                ps.setInt(1, p.getId());
                System.out.println(p.getId());
                rs = ps.executeQuery();
                while (rs.next()) {
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setPhone(rs.getString("phone"));
                    p.setStatus(rs.getString("status"));
                }
            }
            return friendList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendList;
    }

    public String checkLogin(Player p) {
        String sql = "SELECT * FROM tblPlayer WHERE name = ? AND password = ? ";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getName());
            ps.setString(2, p.getPassword());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                p.setId(rs.getInt("id"));
                p.setPhone(rs.getString("phone"));
                p.setStatus(rs.getString("status"));
                return String.valueOf(p.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
        return "false";
    }

}
