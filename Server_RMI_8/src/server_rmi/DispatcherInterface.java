/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_rmi;

import Model.GameMatch;
import Model.Group;
import Model.GroupMember;
import Model.Player;
import Model.PlayerStat;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author demo
 */
public interface DispatcherInterface extends Remote{
    
    // Player management
    public String checkLogin(Player pl) throws RemoteException;
    public String register(Player pl) throws RemoteException;
    public PlayerStat getPlayerStat(int id) throws RemoteException;
    public ArrayList<Player> searchPlayers(String key) throws RemoteException;
    public Player searchPlayers(int key) throws RemoteException;
    public boolean addFriend(Player p1,Player p2) throws RemoteException;
    public boolean updateStatusFriend(Player p1,Player p2) throws RemoteException;
    public boolean cancelFriend(Player p1,Player p2) throws RemoteException;
    public HashMap<Player,String> showFriendRequestPending(Player p) throws RemoteException;
    public ArrayList<Player> SearchFriendList(int id) throws RemoteException;
    public LinkedHashMap<Player,PlayerStat>  getScoreboard() throws RemoteException;
    
    //group management
    public ArrayList<Group> searchAllGroup() throws RemoteException;
    public ArrayList<Group> searchGroup(String key) throws RemoteException;
    public ArrayList<Group> searchGroupJoined(int id) throws RemoteException;
    public boolean cancelMember(GroupMember member) throws RemoteException;
    public boolean addMember(GroupMember member,int g)throws RemoteException;
    public ArrayList<GroupMember> searchMember(String key,int groupID)throws RemoteException;
    
    // Game managment
    public boolean updateGameMatch(GameMatch match) throws RemoteException;
    public ArrayList<GameMatch> searchMatch(String key) throws RemoteException;
    
}
