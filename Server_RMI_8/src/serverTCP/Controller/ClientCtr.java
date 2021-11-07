/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverTCP.Controller;

import Model.GameMatch;
import Model.Group;
import Model.GroupMember;
import Model.IPAddress;
import Model.Player;
import Model.PlayerStat;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import server_rmi.DispatcherInterface;

/**
 *
 * @author demo
 */
public class ClientCtr {
    private DispatcherInterface playerRM;
    private IPAddress serverAddress;
    private String rmiService ;
   

    public ClientCtr() {
       serverAddress = new IPAddress("localhost", 9999); //default server address
        rmiService = "rmiServer";  
    }

    public ClientCtr(String serverHost, int serverPort, String service) {
        serverAddress.setHost(serverHost);
        serverAddress.setPort(serverPort);
        rmiService = service;
    }

    public boolean init() {
        try {
            // get the registry
            Registry registry = LocateRegistry.getRegistry(serverAddress.getHost(), serverAddress.getPort());
            // lookup the remote objects       
            playerRM = (DispatcherInterface) (registry.lookup(rmiService));
        } catch (Exception e) {
            e.printStackTrace();
            // view.showMessage("Error to lookup the remote objects!");
            return false;
        }
        return true;
    }

    // Player management 
    public String checkLogin(Player p) {
        try {
            return playerRM.checkLogin(p);
        } catch (RemoteException e) {
            e.printStackTrace();
            return "false";
        }
    }
    

    public String register(Player p) {
        try {
            return playerRM.register(p);
        } catch (RemoteException e) {
            e.printStackTrace();
            return "false";
        }
    }

    public PlayerStat getPlayerStat(int id) {
        try {
            System.out.println(id + "   2222");
            return playerRM.getPlayerStat(id);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Player> searchPlayers(String key) {
        try {
            return playerRM.searchPlayers(key);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Player searchPlayers(int key) {
        try {
            return playerRM.searchPlayers(key);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addFriend(Player p1, Player p2) {
        try {
            return playerRM.addFriend(p1, p2);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatusFriend(Player p1, Player p2) {
        try {
            return playerRM.updateStatusFriend(p1, p2);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelFrined(Player p1, Player p2) {
        try {
            return playerRM.cancelFriend(p1, p2);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public HashMap<Player, String> showFriendRequestPending(Player p) {
        try {
            return playerRM.showFriendRequestPending(p);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Player> SearchFriendList(int id) {
        try {
            return playerRM.SearchFriendList(id);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LinkedHashMap<Player, PlayerStat> getScoreboard() {
        try {
            return playerRM.getScoreboard();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    // group management 
    public ArrayList<Group> searchGroupJoined(int id) {
        try {
            return playerRM.searchGroupJoined(id);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean cancelMember(GroupMember member) {
        try {
            return playerRM.cancelMember(member);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    // game management 
    public boolean updateGameMatch(GameMatch match) {
        try {
            return playerRM.updateGameMatch(match);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<GameMatch> searchMatch(String key) {
        try {
            return playerRM.searchMatch(key);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
    


  
}
