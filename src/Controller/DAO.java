/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author demo
 */
public class DAO {

    public static Connection conn;

    public DAO() {
        if (conn == null) {
            String db_url = "jdbc:mysql://localhost:3306/GameDB";
            String dbclass = "com.mysql.jdbc.Driver";
            try {
                Class.forName(dbclass);
                conn = DriverManager.getConnection(db_url, "root", "");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    
}