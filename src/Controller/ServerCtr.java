/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.GameMatch;
import Model.Group;
import Model.GroupMember;
import Model.IPAddress;
import Model.MatchRequest;
import Model.ObjectWrapper;
import Model.Player;
import Model.PlayerStat;
import Server.ServerMainFrm;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

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
    private LinkedHashMap<Integer, Integer> playerOnline;
    private LinkedHashMap<Player, PlayerStat> scoreboard;
    private LinkedHashMap<String, List<Player>> listParticipantInARoom;
    private List<MatchRequest> listMatchRequest;
    private static int number = 1;

    public ServerCtr(ServerMainFrm view) {
        myProcess = new ArrayList<ServerProcessing>();
        playerOnline = new LinkedHashMap<>();
        listMatchRequest = new ArrayList<>();
        listParticipantInARoom = new LinkedHashMap<>();

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

//    public void publicListResquestMatch() {
//        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.INVITE_MATCH, listMatchRequest);
//        for (ServerProcessing sp : myProcess) {
//            sp.sendData(data);
//        }
//    }
    public void publicClientNumber() {
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER, myProcess.size());
        for (ServerProcessing sp : myProcess) {
            sp.sendData(data);
        }
    }

    public void sendMatchRequest(MatchRequest mr, int noPlayer) {
        for (ServerProcessing sp : myProcess) {
            if (sp.getNo() == noPlayer) {
                ObjectWrapper data = new ObjectWrapper(ObjectWrapper.INVITE_MATCH, mr);
                sp.sendData(data);
                break;
            }
        }
    }

    public void updateFriendList(Player p, int noPlayer) {
        for (ServerProcessing sp : myProcess) {
            if (sp.getNo() == noPlayer) {
                ObjectWrapper data = new ObjectWrapper(ObjectWrapper.REPLY_FRIEND_LIST, p);
                sp.sendData(data);
                break;
            }
        }
    }

    public void sendUpdateRoom(int noPlayer, List<Player> lp) {
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.UPDATE_ROOM_PLAY, lp);
        for (ServerProcessing sp : myProcess) {
            if (sp.getNo() == noPlayer) {
                sp.sendData(data);
                break;
            }
        }
    }
    
    public void startTheGame(int playerID){
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.REPLY_START_GAME, "play the game" );
        for (ServerProcessing sp : myProcess) {
            if (sp.getNo() == playerOnline.get(playerID)) {
                sp.sendData(data);
                break;
            }
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
    class ServerProcessing extends Thread implements Serializable {

        private Socket mySocket;
        private int no = number++;
        //private ObjectInputStream ois;
        //private ObjectOutputStream oos;

        public ServerProcessing(Socket s) {
            super();
            mySocket = s;
        }

        public int getNo() {
            return no;
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
                                //publicListOnline();
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

                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_FRIEND_LIST, fl));
                                break;
                            case ObjectWrapper.SEARCH_PLAYER_ID:
                                id = (int) data.getData();
                                pd = new PlayerDAO();
                                Player player = pd.searchPlayers(id);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEARCH_PLAYER_ID, player));
                                break;
                            case ObjectWrapper.LOGIN:
                                pl = (Player) data.getData();
                                pd = new PlayerDAO();
                                ok = pd.checkLogin(pl);
                                System.out.println(ok + " check login");
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN, String.valueOf(pl.getId())));
                                    playerOnline.put(pl.getId(), no);
                                    view.showMessage("A player online: " + pl.getName() + " id is " + pl.getId() + " SP number: " + no);
                                    publicListOnline();
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
                                    playerOnline.put(pl.getId(), no);
                                    view.showMessage("A player online: " + pl.getName());
                                    publicListOnline();
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
                                //System.out.println("222" + hm.size());
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
                                ok = pd.updateStatusFriend(friend.get(0), friend.get(1)); // fr0 is sender, fr1 is receiver 
                                if (ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_ANS_FRIEND_REQUEST, "accepted"));
                                    int noPlayer = playerOnline.get(friend.get(0).getId());
                                    updateFriendList(friend.get(0), noPlayer);
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

                            case ObjectWrapper.UPDATE_PLAYER_STATUS:
                                pd = new PlayerDAO();
                                player = (Player) data.getData();
                                ok = pd.editInfo(player);

                                break;
                            case ObjectWrapper.UPDATE_FRIEND_LIST:
                                id = (int) data.getData();
                                pd = new PlayerDAO();
                                fl = pd.SearchFriendList(id);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_FRIEND_LIST, fl));
                                break;
                            case ObjectWrapper.CREATE_A_ROOM:
                                ArrayList<Object> game = (ArrayList<Object>) data.getData();
                                List<Player> listParticipant = new ArrayList<>();
                                listParticipant.add((Player) game.get(1));
                                listParticipantInARoom.put(((GameMatch) game.get(0)).getDescription(), listParticipant);
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_CREATE_ROOM, "ok"));
                                System.out.println("Create a room");
                                break;

                            case ObjectWrapper.INVITE_MATCH:
                                MatchRequest mr = (MatchRequest) data.getData();

                                ok = true;
                                System.out.println("Recievermatch request: " + mr.getReceiver().getId() + mr.getReceiver().getName());
                                for (MatchRequest i : listMatchRequest) {
                                    if (i.getRoomID().equals(mr.getRoomID()) && i.getSender().getName().equals(mr.getSender().getName())
                                            && i.getReceiver().getName().equals(mr.getReceiver().getName())) {
                                        ok = false;
                                    }
                                }
                                System.out.println(ok);
                                if (!ok) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_INVITE_MATCH, "exist"));
                                } else {
                                    listMatchRequest.add(mr);
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_INVITE_MATCH, "ok"));
                                    int spID = playerOnline.get(mr.getReceiver().getId());
                                    System.out.println(spID + " is the port of reciever");
                                    sendMatchRequest(mr, spID);
                                }
                                System.out.println("1111222 " + listMatchRequest.size());
                                break;
                            case ObjectWrapper.ACCEPT_MATCH_REQUEST:
                                mr = (MatchRequest) data.getData();
                                if (listParticipantInARoom.containsKey(mr.getRoomID())) {
                                    listParticipantInARoom.get(mr.getRoomID()).add(mr.getReceiver());
                                    listMatchRequest.remove(mr);
                                    int n = playerOnline.get(mr.getReceiver().getId());
                                    System.out.println("1111222 getting update room");
                                    //sendUpdateRoom(n, listPaticipantInARoom.get(mr.getRoomID()));
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.UPDATE_ROOM_PLAY, listParticipantInARoom.get(mr.getRoomID())));
                                    for (Player players : listParticipantInARoom.get(mr.getRoomID())) {
                                        if (players != mr.getReceiver()) {
                                            n = playerOnline.get(players.getId());
                                            sendUpdateRoom(n, listParticipantInARoom.get(mr.getRoomID()));
                                        }

                                    }
                                }
                            case ObjectWrapper.START_GAME:
                                String room = (String) data.getData();
                                if (listParticipantInARoom.containsKey(room)) {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_START_GAME,null));
                                    for(int i=0;i< listParticipantInARoom.get(room).size();++i){
                                        
                                        startTheGame(listParticipantInARoom.get(room).get(i).getId());
                                    }
                                }
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
                for (MatchRequest mr : listMatchRequest) {
                    if (pl.getName() == mr.getReceiver().getName()) {
                        listMatchRequest.remove(mr);
                    }
                }
                System.out.println("                " + listMatchRequest.size());
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
