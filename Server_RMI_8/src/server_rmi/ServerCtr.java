/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_rmi;

/**
 *
 * @author demo
 */
import Controller.GameMatchDAO;
import Controller.GroupDAO;
import Controller.PlayerDAO;
import Controller.PlayerStatDAO;
import Model.GameMatch;
import Model.Group;
import Model.GroupMember;
import Model.IPAddress;
import Model.MatchRequest;
import Model.Player;
import Model.PlayerStat;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ServerCtr extends UnicastRemoteObject implements DispatcherInterface {

    private IPAddress myAddress = new IPAddress("localhost", 9999);     // default server host/port
    private Registry registry;
    private ServerMainFrm view;
    private String rmiService = "rmiServer";    // default rmi service key

    private List<MatchRequest> listMatchRequest;

    public ServerCtr(ServerMainFrm view) throws RemoteException {
        this.view = view;
        listMatchRequest = new ArrayList<>();
    }

    public ServerCtr(ServerMainFrm view, int port, String service) throws RemoteException {
        this.view = view;
        myAddress.setPort(port);
        this.rmiService = service;
        listMatchRequest = new ArrayList<>();
    }

    public void start() throws RemoteException {
        // registry this to the localhost
        try {
            try {
                //create new one
                registry = LocateRegistry.createRegistry(myAddress.getPort());
            } catch (ExportException e) {//the Registry exists, get it
                registry = LocateRegistry.getRegistry(myAddress.getPort());
            }
            registry.rebind(rmiService, this);
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());
            view.showServerInfo(myAddress, rmiService);
            view.showMessage("The RMI has registered the service key: " + rmiService + ", at the port: " + myAddress.getPort());
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() throws RemoteException {
        // unbind the service
        try {
            if (registry != null) {
                registry.unbind(rmiService);
                UnicastRemoteObject.unexportObject(this, true);
            }
            view.showMessage("The RIM has unbinded the service key: " + rmiService + ", at the port: " + myAddress.getPort());
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public String checkLogin(Player pl) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        String res = new PlayerDAO().checkLogin(pl);
        if (res.equals("false")) {
            return "false";
        } else {
            // check llogin 1 time 
            pl.setId(Integer.parseInt(res));
            return res;
        }
    }

    @Override
    public String register(Player pl) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String res = new PlayerDAO().register(pl);
        if (res.equals("false")) {
            return "false";
        } else {
            pl.setId(Integer.parseInt(res));
            return res;
        }
    }

    @Override
    public PlayerStat getPlayerStat(int id) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new PlayerStatDAO().getPlayerStat(id);
    }

    @Override
    public ArrayList<Player> searchPlayers(String key) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new PlayerDAO().searchPlayers(key);

    }

    @Override
    public Player searchPlayers(int key) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new PlayerDAO().searchPlayers(key);
    }

    @Override
    public boolean addFriend(Player p1, Player p2) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new PlayerDAO().addFriend(p1, p2);
    }

    @Override
    public boolean updateStatusFriend(Player p1, Player p2) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new PlayerDAO().updateStatusFriend(p1, p2);
    }

    @Override
    public HashMap<Player, String> showFriendRequestPending(Player p) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new PlayerDAO().showFriendRequestPending(p);
    }

    @Override
    public ArrayList<Player> SearchFriendList(int id) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new PlayerDAO().SearchFriendList(id);
    }

    @Override
    public LinkedHashMap<Player, PlayerStat> getScoreboard() throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new PlayerStatDAO().getScoreboard();
    }

    @Override
    public ArrayList<Group> searchAllGroup() throws javassist.tools.rmi.RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new GroupDAO().searchAllGroup();
    }

    @Override
    public ArrayList<Group> searchGroup(String key) throws javassist.tools.rmi.RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new GroupDAO().searchGroup(key);
    }

    @Override
    public ArrayList<Group> searchGroupJoined(int id) throws javassist.tools.rmi.RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new GroupDAO().searchGroupJoined(id);
    }

    @Override
    public boolean cancelMember(GroupMember member) throws javassist.tools.rmi.RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new GroupDAO().cancleMember(member);
    }

    @Override
    public boolean addMember(GroupMember member, int g) throws javassist.tools.rmi.RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new GroupDAO().addMember(member, g);
    }

    @Override
    public ArrayList<GroupMember> searchMember(String key, int groupID) throws javassist.tools.rmi.RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new GroupDAO().searchMember(key, groupID);
    }

    @Override
    public boolean updateGameMatch(GameMatch match) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new GameMatchDAO().updateGameMatch(match);
    }

    @Override
    public ArrayList<GameMatch> searchMatch(String key) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new GameMatchDAO().searchMatch(key);
    }

    @Override
    public boolean cancelFriend(Player p1, Player p2) throws RemoteException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new PlayerDAO().cancelFriend(p1, p2);
    }

}
