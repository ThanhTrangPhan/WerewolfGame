/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Group;
import Model.GroupMember;
import Model.IPAddress;
import Model.ObjectWrapper;
import Model.Player;
import Model.PlayerStat;
import Server.ServerMainFrm;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author demo
 */
public class ServerCtr {

    private ServerMainFrm view;
    private ServerSocket myServer;
    private ServerListening myListening;
    private ArrayList<ServerProcessing> myProcess;
    private IPAddress myAddress = new IPAddress("localhost", 8998);  //default server host and port
    private List<Integer> playerOnline;
    private LinkedHashMap<Player, PlayerStat> scoreboard;

    public ServerCtr(ServerMainFrm view) {
        myProcess = new ArrayList<ServerProcessing>();
        playerOnline = new ArrayList<>();
        this.view = view;
        openServer();
    }

    public ServerCtr(ServerMainFrm view, int serverPort) {
        myProcess = new ArrayList<ServerProcessing>();
        this.view = view;
        myAddress.setPort(serverPort);
        openServer();
    }

    private void openServer() {
        try {
            myServer = new ServerSocket(myAddress.getPort());
            myListening = new ServerListening();
            myListening.start();
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());
            view.showServerInfor(myAddress);
            //System.out.println("server started!");
            view.showMessage("TCP server is running at the port " + myAddress.getPort() + "...");
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }

    public void stopServer() {
        try {
            for (ServerProcessing sp : myProcess) {
                sp.stop();
            }
            myListening.stop();
            myServer.close();
            view.showMessage("TCP server is stopped!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // update the online Player 
    public void publicListOnline() {
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.PLAYER_ONLINE, playerOnline);
        for (ServerProcessing sp : myProcess) {
            sp.sendData(data);
        }
        data = new ObjectWrapper(ObjectWrapper.GLOBAL_SCOREBOARD, scoreboard);
        for (ServerProcessing sp : myProcess) {
            sp.sendData(data);
        }
    }

    public void publicClientNumber() {
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER, myProcess.size());
        for (ServerProcessing sp : myProcess) {
            sp.sendData(data);
        }
    }

    /**
     * The class to listen the connections from client, avoiding the blocking of
     * accept connection
     *
     */
    class ServerListening extends Thread {

        public ServerListening() {
            super();
        }

        public void run() {
            view.showMessage("server is listening... ");
            try {
                while (true) {
                    Socket clientSocket = myServer.accept();
                    ServerProcessing sp = new ServerProcessing(clientSocket);
                    sp.start();
                    myProcess.add(sp);
                    view.showMessage("Number of client connecting to the server: " + myProcess.size());

                    publicClientNumber();
                    publicListOnline();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The class to treat the requirement from client
     *
     */
    class ServerProcessing extends Thread {

        private Socket mySocket;
        //private ObjectInputStream ois;
        //private ObjectOutputStream oos;

        public ServerProcessing(Socket s) {
            super();
            mySocket = s;
        }

        public void sendData(Object obj) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
                oos.writeObject(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            Player pl = new Player();
            try {
                while (true) {

                    ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
                    GroupDAO gd;
                    boolean ok = false;
                    Object o = ois.readObject();
                    if (o instanceof ObjectWrapper) {
                        ObjectWrapper data = (ObjectWrapper) o;
                        System.out.println(data.getPerformative());
                        switch (data.getPerformative()) {
                            case ObjectWrapper.PLAYER_ONLINE:
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.PLAYER_ONLINE, playerOnline));
                                publicListOnline();
                                break;

                            case ObjectWrapper.CREATE_GROUP:
                                Group g = (Group) data.getData();
                                gd = new GroupDAO();
                                ok = gd.createGroup(g);
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_CREATE_GROUP, "ok"));
                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_CREATE_GROUP, "false"));
                                }
                                break;
                            case ObjectWrapper.SEARCH_GROUP:
                                String key = (String) data.getData();
                                gd = new GroupDAO();
                                ArrayList<Group> result = gd.searchGroup(key);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEARCH_GROUP, result));
                                break;
                            case ObjectWrapper.ADD_MEMBER:
                                GroupMember gm = (GroupMember) data.getData();
                                int groupId = (int) data.getData();
                                gd = new GroupDAO();
                                ok = gd.addMember(gm, groupId);

                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_ADD_MEMBER, "ok"));
                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_ADD_MEMBER, "false"));
                                }
                                break;
                            case ObjectWrapper.CANCLE_MEMBER:
                                GroupMember memberID = (GroupMember) data.getData();
                                gd = new GroupDAO();
                                ok = gd.cancleMember(memberID);
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_CANCLE_MEMBER, "ok"));
                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_CANCLE_MEMBER, "false"));
                                }
                                break;

                            case ObjectWrapper.SEARCH_MEMBER:
                                key = (String) data.getData();
                                int gr = (int) data.getData();
                                gd = new GroupDAO();
                                ArrayList<GroupMember> res = gd.searchMember(key, gr);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEARCH_GROUP, res));
                                break;
                            case ObjectWrapper.SEARCH_GROUP_JOINED:
                                int id = (int) data.getData();
                                gd = new GroupDAO();
                                ArrayList<Group> ress = gd.searchGroupJoined(id);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEARCH_GROUP, ress));
                                break;
                            case ObjectWrapper.FRIEND_LIST:
                                id = (int) data.getData();
                                PlayerDAO pd = new PlayerDAO();
                                ArrayList<Player> fl = pd.SearchFriendList(id);
                                System.out.println(fl);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_FRIEND_LIST, fl));
                                break;
                            case ObjectWrapper.SEARCH_PLAYER_ID:
                                id = (int) data.getData();
                                pd = new PlayerDAO();
                                Player player = pd.searchPlayers(id);

                                break;
                            case ObjectWrapper.LOGIN:
                                pl = (Player) data.getData();
                                pd = new PlayerDAO();
                                ok = pd.checkLogin(pl);
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN, String.valueOf(pl.getId())));
                                    playerOnline.add(pl.getId());
                                    view.showMessage("A player online: " + pl.getName() + "id is" + pl.getId());

                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN, "false"));
                                }

                                break;
                            case ObjectWrapper.REGISTER:
                                pl = (Player) data.getData();
                                pd = new PlayerDAO();
                                ok = pd.register(pl);
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_REGISTER, String.valueOf(pl.getId())));
                                    playerOnline.add(pl.getId());
                                    view.showMessage("A player online: " + pl.getName());

                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_REGISTER, "false"));
                                }
                                break;
                            case ObjectWrapper.STATISTIC:
                                id = (int) data.getData();
                                PlayerStatDAO psd = new PlayerStatDAO();
                                PlayerStat ps = psd.getPlayerStat(id);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_STATISTIC, ps));
                                break;
                            case ObjectWrapper.GLOBAL_SCOREBOARD:
                                psd = new PlayerStatDAO();
                                LinkedHashMap<Player, PlayerStat> hm = psd.getScoreboard();
                                scoreboard = hm;
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.GLOBAL_SCOREBOARD, hm));
                                break;
                            case ObjectWrapper.LOAD_FRIEND_REQUEST:
                                pd = new PlayerDAO();
                                pl = (Player) data.getData();
                                HashMap<Player, String> req = pd.showFriendRequestPending(pl);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_LOAD_FRIEND_REQUEST, req));
                                break;
                            case ObjectWrapper.ACCEPT_FRIEND_REQUEST:
                                pd = new PlayerDAO();
                                ArrayList<Player> friend = (ArrayList<Player>) data.getData();
                                ok = pd.updateStatusFriend(friend.get(0), friend.get(1));
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_ANS_FRIEND_REQUEST, "accepted"));

                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_ANS_FRIEND_REQUEST, "false"));
                                }
                                break;
                            case ObjectWrapper.REJECT_FRIEND_REQUEST:
                                pd = new PlayerDAO();
                                friend = (ArrayList<Player>) data.getData();
                                ok = pd.cancelFriend(friend.get(0), friend.get(1));
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_ANS_FRIEND_REQUEST, "rejected"));

                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_ANS_FRIEND_REQUEST, "false"));
                                }
                                break;

                            case ObjectWrapper.SEND_FRIEND_REQUEST:
                                pd = new PlayerDAO();
                                friend = (ArrayList<Player>) data.getData();
                                ok = pd.addFriend(friend.get(0), friend.get(1));
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEND_FRIEND_REQUEST, "ok"));

                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEND_FRIEND_REQUEST, "false"));
                                }
                                break;

                        }

                    }
                    //ois.reset();
                    //oos.reset();
                }
            } catch (EOFException | SocketException e) {
                //e.printStackTrace();
                playerOnline.remove(new Integer(pl.getId()));
                view.showMessage("A player logout: " + pl.getName());
                myProcess.remove(this);
                view.showMessage("Number of client connecting to the server: " + myProcess.size());
                publicClientNumber();
                publicListOnline();
                try {
                    mySocket.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                this.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
